package utsw.bicf.answer.security;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;

public class LDAPAuthentication {
	
	String username;
	String password;
	String url;
	Hashtable<String, String> env = new Hashtable<String, String>();
	
	private void init() {
		// Set up the environment for creating the initial context
		env.put(Context.INITIAL_CONTEXT_FACTORY, 
		    "com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, "ldap://" + url);

		// Authenticate
		env.put(Context.SECURITY_AUTHENTICATION, "simple");
		env.put(Context.SECURITY_PROTOCOL, "tls");
		env.put(Context.SECURITY_PRINCIPAL, "cn=" + username.toLowerCase() + ",ou=services, dc=swmed, dc=org");
		env.put(Context.SECURITY_CREDENTIALS, password);
		env.put(Context.REFERRAL, "follow");
	}

	public boolean isUserValid(String userLogin, String userPassword) {
		init();
		DirContext ctx = null;
		try {
			if (userPassword == null || userPassword.equals("")) {
				return false;
			}
			ctx = new InitialDirContext(env);
			SearchControls ctls = new SearchControls();
			ctls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			ctls.setCountLimit(1);
			
			//search for user
			String filter = "(cn=" + userLogin.replaceAll("\\W", "") + ")";
			String name = "dc=swmed,dc=org";
			NamingEnumeration<SearchResult> results = ctx.search(name, filter, ctls);
			while (results != null && results.hasMore()) {
				SearchResult result = results.next();
				Attributes attrs = result.getAttributes();
				String dn = (String) attrs.get("distinguishedName").get();
				env.put(Context.SECURITY_PRINCIPAL, dn);
				env.put(Context.SECURITY_CREDENTIALS, userPassword);
				//checks that the new credentials are working
				new InitialDirContext(env);
				return true;
			}
		} catch (NamingException e) {
//			e.printStackTrace();
			return false; //ldap authentication failed. Wrong password or other server issue
		} finally {
			try {
				if (ctx != null)
					ctx.close();
			} catch (NamingException e) {
//				e.printStackTrace();
				return false; //could not close the context for some reason. Don't let user log in just in case.
			}
		}
		return false; //should only reach this spot is username is wrong.
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
