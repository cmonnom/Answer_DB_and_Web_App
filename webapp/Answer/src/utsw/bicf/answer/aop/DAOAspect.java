package utsw.bicf.answer.aop;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.owasp.html.HtmlPolicyBuilder;
import org.owasp.html.PolicyFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import utsw.bicf.answer.controller.ControllerUtil;
import utsw.bicf.answer.controller.serialization.TargetPage;
import utsw.bicf.answer.model.User;
import utsw.bicf.answer.security.PermissionUtils;

@Aspect
@Component
public class DAOAspect {

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
				String sanitized = policy.sanitize(argString);
				if (!argString.equals(sanitized)) {
					currentArgIsValid = false;
					//check that it's a valid Json
					ObjectMapper mapper = new ObjectMapper();
					JsonNode jsonNodeArray = mapper.readTree(argString);
					if (jsonNodeArray != null) {
						currentArgIsValid = true; //rule exception for Json strings
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

	@Around("controllerPackage() && !loginOrErrorController()")
	public String handleUserPermission(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
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
		String result = null;
		if (httpSession != null && model != null) {
			boolean isAjax = (boolean) model.asMap().get("isAjax");
			boolean isAllowed = (boolean) model.asMap().get("isAllowed");
			boolean isXss = (boolean) model.asMap().get("isXss");
			// proceed regardless of allowed for non-ajax calls to set redirects properly
			// unless it's xss
			if (isXss && !isAjax) {
				ControllerUtil.initializeModel(model, servletContext);
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
				if (isAjax) {
					TargetPage targetPage = new TargetPage(model);
					return targetPage.toJSONString(); // build an error object as ajax response
				} else {
					return ControllerUtil.initializeModelLogin(model, servletContext, method);
				}
			} else if (canProceed) {
				result = (String) proceedingJoinPoint.proceed();
				return result;
			} else { //cannot proceed
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
