package jobtitles;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        StringBuilder builder = new StringBuilder(
                "Welcome to the job normalizer program!\n" +
                "This program will take a string input and map it to one of the following professions:\n");
        for (JobTitle title : JobTitle.values()) {
            builder.append("\t");
            builder.append(title.getDisplayName());
            builder.append("\n");
        }
        builder.append("\nPlease Enter a Profession:");
        System.out.println(builder);
        Scanner scanner = new Scanner(System.in);
        String inputString = scanner.next();
        Normalizer normalizer = new Normalizer();
        String outputString = normalizer.normalize(inputString);
        System.out.println("The system believes the job you entered best matches this profession:");
        System.out.println("\t" + outputString);
    }
}