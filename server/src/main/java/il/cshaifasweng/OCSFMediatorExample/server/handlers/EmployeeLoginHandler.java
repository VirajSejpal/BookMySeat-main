package il.cshaifasweng.OCSFMediatorExample.server.handlers;
import il.cshaifasweng.OCSFMediatorExample.entities.Employee;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.EmployeeLoginMessage;
import il.cshaifasweng.OCSFMediatorExample.entities.Messages.LoginMessage;
import il.cshaifasweng.OCSFMediatorExample.server.ocsf.ConnectionToClient;
import org.hibernate.Session;
import org.hibernate.query.Query;


public class EmployeeLoginHandler extends LoginHandler {

    private EmployeeLoginMessage message;

    public EmployeeLoginHandler(EmployeeLoginMessage message, ConnectionToClient client, Session session)
    {
        super(message,client,session);
        this.message = message;
    }

    protected void login()
    {
        try {
            // Create a query to find the user by ID
            Query<Employee> query = session.createQuery("FROM Employee WHERE id_number = :id AND password = :password", Employee.class);
            query.setParameter("id", message.id);
            query.setParameter("password", message.password);

            // Execute the query and get the result
            Employee employee = query.uniqueResult();

            // Check if the user exists and verify the login credentials
            if (employee != null) {
                // Check if the user is already logged in
                if (employee.isOnline()) {
                    message.responseType = LoginMessage.ResponseType.ALREADY_LOGGED;
                } else {
                    // Set the user as online
                    employee.setOnline(true);
                    session.update(employee);
                    session.flush();
                    message.responseType = LoginMessage.ResponseType.LOGIN_SUCCESFUL;
                    message.employeeType = employee.getEmployeeType();
                }
            } else {
                message.responseType = LoginMessage.ResponseType.LOGIN_FAILED;
            }
        } catch (Exception e) {
            // Rollback the transaction in case of error
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            message.responseType = LoginMessage.ResponseType.LOGIN_FAILED;
            e.printStackTrace();
        }

    }
    protected void logout()
    {
        try {
            // Create a query to find the user by ID
            Query<Employee> query = session.createQuery("FROM Employee WHERE id_number = :id", Employee.class);
            query.setParameter("id", message.id);
           // query.setParameter("password", message.password);

            // Execute the query and get the result
            Employee employee = query.uniqueResult();

            // Check if the user exists and is currently logged in
            if (employee != null) {
                if (employee.isOnline()) {
                    // Set the user as offline
                    employee.setOnline(false);
                    session.update(employee);
                    session.flush();
                    message.responseType = LoginMessage.ResponseType.LOGOUT_SUCCESFUL;

                } else {
                    message.responseType = LoginMessage.ResponseType.LOGOUT_FAILED;
                }
            } else {
                message.responseType = LoginMessage.ResponseType.LOGOUT_FAILED;
            }
        } catch (Exception e) {
            // Rollback the transaction in case of error
            if (session.getTransaction().isActive()) {
                session.getTransaction().rollback();
            }
            message.responseType = LoginMessage.ResponseType.LOGOUT_FAILED;
            e.printStackTrace();
        }
    }

}
