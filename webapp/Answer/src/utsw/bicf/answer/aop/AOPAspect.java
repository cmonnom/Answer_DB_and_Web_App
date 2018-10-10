package utsw.bicf.answer.aop;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.ControllerUtil;
import utsw.bicf.answer.controller.OpenCaseController;
import utsw.bicf.answer.controller.serialization.TargetPage;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.security.PermissionUtils;

@Aspect
@Component
public class AOPAspect {

	private static final Logger logger = Logger.getLogger(AOPAspect.class);
	
	private static final List<String> METHODS_SKIP_SANITATION = new ArrayList<String>();
	static {
		METHODS_SKIP_SANITATION.add("commitAnnotations");
	}
	
	@Autowired
	ServletContext servletContext;

	@Pointcut("execution(* utsw.bicf.answer.controller.*.*(..))")
	private void controllerPackage() {
	}

	@Pointcut("execution(* utsw.bicf.answer.controller.LoginController.*(..))")
	private void loginController() {
	}

	@Pointcut("execution(* utsw.bicf.answer.controller.ErrorController.*(..))")
	private void errorController() {
	}

	@Pointcut("loginController() || errorController()")
	private void loginOrErrorController() {
	}

	@Pointcut("execution(* utsw.bicf.answer.controller.api.APIController.*(..))")
	private void apiController() {
	}

	public void checkUserPermission(JoinPoint joinPoint, Model model, HttpSession httpSession) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		boolean isAjax = method.getAnnotation(ResponseBody.class) != null;

		if (httpSession != null && model != null) {
			User user = (User) httpSession.getAttribute("user");
			model.addAttribute("isAjax", isAjax);
			if (user == null) {
				model.addAttribute("isAllowed", false);
				model.addAttribute("isLogin", true);
				model.addAttribute("reason", "Please log in again");
			} else {
				model.addAttribute("isAllowed", true);
			}
		}

	}

	/**
	 * Strips any user input (String) of html tags. This should prevent XSS attacks
	 * and should be applied to every user input
	 * 
	 * @param joinPoint
	 * @throws Exception
	 */
	public void checkUserInput(JoinPoint joinPoint, Model model) throws Exception {
		PolicyFactory policy = new HtmlPolicyBuilder().toFactory();
		Object[] args = joinPoint.getArgs();
		boolean isValid = true;
		for (Object arg : args) {
			if (arg instanceof String) {
				boolean currentArgIsValid = true;
				String argString = (String) arg;
				String sanitized = policy.sanitize(argString).replaceAll("&#64;", "@"); // emails are ok
				if (!argString.equals(sanitized) && !skipSanitation(joinPoint)) {
					// check that it's a valid Json
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNodeArray = mapper.readTree(argString);
					if (jsonNodeArray != null) {
						// make sure there is no funny business inside the json object
						currentArgIsValid &= sanitizeJson(policy, jsonNodeArray);
					}
				}
				isValid &= currentArgIsValid;
			}
		}
		if (!isValid) {
			model.addAttribute("isAllowed", false);
			model.addAttribute("isXss", true);
			model.addAttribute("reason", "Invalid characters");
		} else {
			model.addAttribute("isXss", false);
		}
	}

	
	/**
	 * Some text fields could contain special characters and still be ok.
	 * They wouldn't need to be sanitized because they are escaped when stored in the database
	 * @param joinPoint
	 * @return
	 */
	private boolean skipSanitation(JoinPoint joinPoint) {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		return METHODS_SKIP_SANITATION.contains(method.getName());
		
	}

	private static boolean sanitizeJson(PolicyFactory policy, JsonNode tree) {
		boolean isValid = true;
		if (tree.isTextual()) {
			String argString = tree.textValue();
			String sanitized = policy.sanitize(argString)
					.replaceAll("&#64;", "@")
					.replaceAll("&gt;", ">")
					.replaceAll("&amp;", "&")
					.replaceAll("&#43;", "+"); // emails are ok
			isValid &= argString.equals(sanitized);
			return isValid;
		} else if (tree.isNumber()) {

		} else {
			for (JsonNode node : tree) {
				isValid &= sanitizeJson(policy, node);
			}
		}
		return isValid;
	}
	
	@Before("controllerPackage() || loginOrErrorController() || apiController()")
	public void logBeforeMethod(JoinPoint joinPoint) throws IOException {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		Object[] args = joinPoint.getArgs();
		HttpSession httpSession = null;
		for (Object arg : args) {
			if (arg instanceof HttpSession) {
				httpSession = (HttpSession) arg;
				break;
			}
		}
		User user = (User) httpSession.getAttribute("user");
		if (user != null) {
			boolean canProceed = PermissionUtils.canProceed(user, method);
			logger.info("@Before ===> UserId: " + user.getUserId() + " Username: " + user.getUsername() + " Accessing: " + method.getName() + " Allowed:" + canProceed);
		}
		else {
			//user needs to login
			logger.info("@Before ===> User was not logged in when accessing: " +  method.getName());
		}
	}
	
	@AfterReturning(pointcut="controllerPackage() || loginOrErrorController() || apiController()", returning="result")
	public void logAfterMethod(JoinPoint joinPoint, Object result) throws IOException {
		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		Method method = signature.getMethod();
		String[] argNames = signature.getParameterNames();
		Object[] args = joinPoint.getArgs();
		HttpSession httpSession = null;
		Model model = null;
		String caseId = null;
		for (Object arg : args) {
			if (arg instanceof HttpSession) {
				httpSession = (HttpSession) arg;
			} else if (arg instanceof Model) {
				model = (Model) arg;
			}
		}
		User user = (User) httpSession.getAttribute("user");
		
		if (user != null) {
			boolean canProceed = PermissionUtils.canProceed(user, method);
			StringBuilder logMessage = new StringBuilder("@AfterReturning ===> UserId: ");
			logMessage.append(user.getUserId()).append(" Username: ").append(user.getUsername()).append(" Accessing: ").append( method.getName());
			int argIndex = -1;
			for (int i = 0; i < argNames.length; i++) {
				if (argNames[i].equals("caseId")) {
					argIndex = i;
					break;
				}
			}
			if (argIndex > -1) {
				logMessage.append(" CaseId: ").append(args[argIndex]);
			}
			if (model != null && model.asMap().containsKey("isAjax")) {
				boolean isAjax = (boolean) model.asMap().get("isAjax");
				boolean isAllowed = (boolean) model.asMap().get("isAllowed");
				boolean isXss = (boolean) model.asMap().get("isXss");
				if (isAjax && result instanceof String) {
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNodeArray = mapper.readTree(result.toString());
					if (jsonNodeArray != null && jsonNodeArray.get("isAllowed") != null) {
						canProceed &= jsonNodeArray.get("isAllowed").booleanValue();
					}
				}
				logMessage.append(" Allowed:").append(canProceed);
				if (!isAjax && result instanceof String) {
					logMessage.append(" Sent to ").append("'").append(result).append("' page");
				}
			}
			logger.info(logMessage.toString());
			logger.info("------------------------");
		}
		else {
			//user needs to login
			logger.info("@AfterReturning ===> User was not logged in and was denied access when accessing: " +  method.getName());
			logger.info("------------------------");
		}
	}
	

	@Around("controllerPackage() && !loginOrErrorController() && !apiController()")
	public Object handleUserPermission(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		Object[] args = proceedingJoinPoint.getArgs();
		HttpSession httpSession = null;
		Model model = null;
		for (Object arg : args) {
			if (arg instanceof HttpSession) {
				httpSession = (HttpSession) arg;
			} else if (arg instanceof Model) {
				model = (Model) arg;
			}
		}
		checkUserPermission(proceedingJoinPoint, model, httpSession);
		checkUserInput(proceedingJoinPoint, model);
		if (httpSession != null && model != null) {
			boolean isAjax = (boolean) model.asMap().get("isAjax");
			boolean isAllowed = (boolean) model.asMap().get("isAllowed");
			boolean isXss = (boolean) model.asMap().get("isXss");
			// proceed regardless of allowed for non-ajax calls to set redirects properly
			// unless it's xss
			if (isXss && !isAjax) {
				ControllerUtil.initializeModel(model, servletContext, null);
				return "error";
			} else if (isXss && isAjax) {
				TargetPage targetPage = new TargetPage(model);
				return targetPage.toJSONString(); // build an error object as ajax response
			}
			// At this point user input should be clean
			MethodSignature signature = (MethodSignature) proceedingJoinPoint.getSignature();
			Method method = signature.getMethod();
			User user = (User) httpSession.getAttribute("user");
			boolean canProceed = PermissionUtils.canProceed(user, method);

			if (!isAllowed) {
				if (method.getReturnType().equals(ResponseEntity.class)) {
					TargetPage targetPage = new TargetPage(model);
					ResponseEntity<String> response = new ResponseEntity<String>(targetPage.toJSONString(), HttpStatus.FORBIDDEN);
					return response;
				}
				if (isAjax) {
					TargetPage targetPage = new TargetPage(model);
					return targetPage.toJSONString(); // build an error object as ajax response
				} else {
					proceedingJoinPoint.proceed(); // need to run the method to initialize the urlRedirect
					return ControllerUtil.initializeModelLogin(model, servletContext, method);
				}
			} else if (canProceed) {
				return proceedingJoinPoint.proceed();
			} else { // cannot proceed
				model.addAttribute("isAjax", false);
				return ControllerUtil.initializeModelNotAllowed(model, servletContext);
			}

		} else {
			// model of session is null which indicates an error on the source code side.
			// for instance the controller method doesn't pass model and session
			// We should return the user to the login page.
			return ControllerUtil.initializeModelLogin(model, servletContext, null);
		}

	}

}
