package sample.main;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTestHarness {

    public static void main(String[] args) {
//        Console console = System.console();
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        if (console == null) {
            System.err.println("No console.");
            System.exit(1);
        }
        while (true) {
            Pattern pattern = null;
            try {
                System.out.println("%nEnter your regex: ");
                pattern = Pattern.compile(console.readLine());
                System.out.println("Enter input string to search: ");
                Matcher matcher = pattern.matcher(console.readLine());
                boolean found = false;
                while (matcher.find()) {
                    System.out.format("I found the text" +
                                    " \"%s\" starting at " +
                                    "index %d and ending at index %d.%n",
                            matcher.group(),
                            matcher.start(),
                            matcher.end());
                    found = true;
                }
                if (!found) {
                    System.out.format("No match found.%n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
