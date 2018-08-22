package utsw.bicf.answer.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.dao.DuplicateKeyException;

import utsw.bicf.answer.model.IndividualPermission;
import utsw.bicf.answer.model.User;

public class PermissionUtils {
	
	IndividualPermission permission;
	List<String> fields;
	
	//Manage permission of every url by adding the name of the method
	//and the permissions to permissionPerUrl (see HomeController)
	private static final Map<String, List<String>> permissionPerUrl = new HashMap<String, List<String>>();

	
	public PermissionUtils(List<String> fields) {
		super();
		this.fields = fields;
	}
	
	public static void addPermission(String method, List<String> fields) throws DuplicateKeyException {
		if (permissionPerUrl.containsKey(method)) {
			throw new DuplicateKeyException("This method has already been added");
		}
		permissionPerUrl.put(method, fields);
	}
	
	public static void addPermission(String method, String field) throws DuplicateKeyException {
		if (permissionPerUrl.containsKey(method)) {
			throw new DuplicateKeyException("This method has already been added");
		}
		List<String> fields = new ArrayList<String>();
		fields.add(field);
		permissionPerUrl.put(method, fields);
	}

	public static boolean canProceed(User user, Method method) throws IOException {
		if (user == null) {
			return false;
		}
		List<String> fields = permissionPerUrl.get(method.getDeclaringClass().getCanonicalName() + "." + method.getName());
		IndividualPermission permission = user.getIndividualPermission();
		if (user != null && permission.getAdmin()) {
			return true;
		}
		if (fields != null && !fields.isEmpty()) {
			boolean canProceed = true;
			for (String field : fields) {
				switch(field) {
				case IndividualPermission.CAN_VIEW: canProceed &= permission.getCanView(); break; 
				case IndividualPermission.CAN_ANNOTATE: canProceed &= permission.getCanAnnotate(); break; 
				case IndividualPermission.CAN_SELECT: canProceed &= permission.getCanSelect(); break; 
				case IndividualPermission.CAN_ASSIGN: canProceed &= permission.getCanAssign(); break; 
				case IndividualPermission.CAN_REVIEW: canProceed &= permission.getCanReview(); break; 
				default: canProceed = false; break;
				}
				
			}
			return canProceed;
		}
		return false; //permissions have not been set on this method
	}
	
}
