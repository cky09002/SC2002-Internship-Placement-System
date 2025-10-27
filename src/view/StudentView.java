package view;

import model.Application;

import java.util.Map;
import java.util.Scanner;

public class StudentView {
    // TODO: move to View base class
    public static final Scanner sc = new Scanner(System.in);

    public MenuOption showMenuDialog(Map<String, MenuOption> dialogOptionMap) {
        System.out.println("=== Main menu ===");

        // print all dialog options (key and description)
        for (Map.Entry<String, MenuOption> entry : dialogOptionMap.entrySet()) {
            System.out.printf("%s : %s%n", entry.getKey(), entry.getValue().getDesc());
        }
        System.out.println("\nChoose a menu option:");

        // get user option input
        // returns null if wrong input
        String sel = sc.next();
        return dialogOptionMap.get(sel);
    }

    public void showInternships() {
        System.out.println("=== Internships ===");
        // TODO
    }

    public int showApplyDialog() {
        System.out.println("=== Submit application ===");
        System.out.println("Choose internship to apply for (enter internship ID):");
        return sc.nextInt();
    }

    public int showWithdrawDialog() {
        System.out.println("=== Withdraw application ===");
        System.out.println("Choose application to withdraw (enter application ID):");
        return sc.nextInt();
    }

    public int showAcceptDialog() {
        System.out.println("=== Accept application ===");
        System.out.println("Choose application to accept (enter application ID):");
        return sc.nextInt();
    }

    public void showApplyMsg(Application submittedData) {
        System.out.println("--- Application submitted ---");
        System.out.println("Internship details:");
        System.out.printf("  ID: %d%n", submittedData.internship.id);
        System.out.printf("  Title: %s%n", submittedData.internship.title);
        System.out.printf("  Description: %s%n", submittedData.internship.desc);
        // ...
        System.out.println("Application details:");
        System.out.printf("  ID: %d%n", submittedData.id);
        System.out.printf("  Applicant: %s%n", submittedData.applicant);
        System.out.printf("  Date applied: %s%n", submittedData.dateApplied);
        System.out.printf("  Status: %s%n", submittedData.status);
    }
}
