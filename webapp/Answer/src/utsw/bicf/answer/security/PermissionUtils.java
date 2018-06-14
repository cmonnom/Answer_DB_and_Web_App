package utsw.bicf.answer.security;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.dao.DuplicateKeyException;

import utsw.bicf.answer.controller.HomeController;
import utsw.bicf.answer.model.Permission;
import utsw.bicf.answer.model.User;

public class PermissionUtils {
	
	//the controller should explicitly give permission to proceed
	public static final String CONTROLLER_ALLOWED = "controllerAllowed";
	public static final String CAN_VIEW = "canView";
	public static final String CAN_EDIT = "canEdit";
	public static final String CAN_FINALIZE = "canFinalize";
	
	boolean canView;
	boolean canEdit;
	boolean canFinalize;
	
	//Manage permission of every url by adding the name of the method
	//and the permissions to permissionPerUrl (see HomeController)
	private static final Map<String, PermissionUtils> permissionPerUrl = new HashMap<String, PermissionUtils>();

	
	public PermissionUtils(boolean canView, boolean canEdit, boolean canFinalize) {
		super();
		this.canView = canView;
		this.canEdit = canEdit;
		this.canFinalize = canFinalize;
	}
	
	public static void addPermission(String method, PermissionUtils permission) throws DuplicateKeyException {
		if (permissionPerUrl.containsKey(method)) {
			throw new DuplicateKeyException("This method has already been added");
		}
		permissionPerUrl.put(method, permission);
	}

	public static boolean canProceed(User user, Method method) throws IOException {
		PermissionUtils controllerPermission = permissionPerUrl.get(method.getDeclaringClass().getCanonicalName() + "." + method.getName());
		if (user != null && user.getPermission().getAdmin()) {
			return true;
		}
		if (controllerPermission != null) {
			return canProceed(user, controllerPermission.canView, controllerPermission.canEdit, controllerPermission.canFinalize);
		}
		return false; //permissions have not been set on this method
	}
	
	public static boolean canProceed(User user, boolean canView, boolean canEdit, boolean canFinalize) throws IOException {
		if (user == null) {
			return false; //should have been tested for null before
		}
		Permission userPermissions = user.getPermission();
		boolean isAllowed = false;
		if (canView) {
			isAllowed = userPermissions.getView();
		}
		if (canEdit) {
			isAllowed &= userPermissions.getEdit();
		}
		if (canFinalize) {
			isAllowed &= userPermissions.getFinalize();
		}
		return isAllowed;
	}
}
