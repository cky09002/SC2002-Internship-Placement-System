package utils.formatter;

import model.*;

/**
 * Default implementation for user/profile formatting.
 * Provides flexible formatting options for displaying user profiles.
 * Can be extended for different formatting styles.
 */
public class UserFormatter implements Formatter {
    
    /**
     * Default constructor for UserFormatter.
     */
    public UserFormatter() {
        // Default constructor
    }
    
    @Override
    public String format(Object object) {
        if (!(object instanceof User)) {
            throw new IllegalArgumentException("Object must be a User");
        }
        
        return formatProfile((User) object);
    }
    
    /**
     * Static utility method for formatting user profile
     * @param user The user to format
     * @return Formatted profile string
     */
    public static String formatProfile(User user) {
        StringBuilder sb = new StringBuilder();
        sb.append(user.getUserType()).append(" | ID: ").append(user.getUserID())
          .append(" | Name: ").append(user.getName())
          .append(" | Email: ").append(user.getEmail());
        
        String type = user.getUserType();
        if (type.equals("Student")) {
            Student s = (Student) user;
            sb.append(" | Year: ").append(s.getYearOfStudy()).append(" | Major: ").append(s.getMajor());
        } else if (type.equals("CompanyRepresentative")) {
            CompanyRepresentative cr = (CompanyRepresentative) user;
            sb.append(" | Company: ").append(cr.getCompanyName())
              .append(" | Dept: ").append(cr.getDepartment())
              .append(" | Position: ").append(cr.getPosition())
              .append(" | Status: ").append(cr.getApprovalStatus());
        } else if (type.equals("Staff")) {
            sb.append(" | Dept: ").append(((Staff) user).getStaffDepartment());
        }
        return sb.toString();
    }
}

