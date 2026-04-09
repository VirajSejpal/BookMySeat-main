package il.cshaifasweng.OCSFMediatorExample.entities.Messages;

import il.cshaifasweng.OCSFMediatorExample.entities.Employee;

public class EmployeeLoginMessage extends LoginMessage
{
    public String password;
    public Employee.EmployeeType employeeType;

    /**
     * Ctor that support LOGIN and LOGOUT.
     *
     * @param id is the id of employee
     * @param type is the MessageType
     * @param requestType is what request we want
     * @param password is the password of the employee
     */
    public EmployeeLoginMessage(String id, MessageType type, RequestType requestType, String password)
    {
        // LOGIN , LOGOUT
        super(id, type, requestType);
        this.password = password;
    }
}
