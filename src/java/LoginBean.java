import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

@ManagedBean
@SessionScoped
public class LoginBean {
    private String username;
    private String password;

    // Getter and setter methods for username and password
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

    public String login() {
        // Perform your login logic here
        if (username.equals("username") && password.equals("password")) {
            // Set user authentication state
            FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put("loggedIn", true);
            return "index.xhtml"; // Redirect to a success page
        } else {
            // Invalid credentials, show an error message
            return "greska.xhtml"; // Redirect to an error page
        }
    }
}
