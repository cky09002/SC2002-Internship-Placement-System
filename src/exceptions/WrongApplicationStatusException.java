package exceptions;

import constant.ApplicationStatus;

public class WrongApplicationStatusException extends RuntimeException {
    public WrongApplicationStatusException(ApplicationStatus curStatus) {
        super(String.format("Application is %s, needs to be %s to be accepted.", curStatus, ApplicationStatus.SUCCESSFUL));
    }
}
