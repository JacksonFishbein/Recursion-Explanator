import java.util.Scanner;

public class PalindromeTest {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter a word to check if it is a palindrome: ");
        String word = scanner.nextLine();

        // Ignore capitalization
        word = word.toLowerCase();

        if (isPalindrome(word, 0, word.length() - 1)) {
            System.out.println(word + " is a palindrome!");
        } else {
            System.out.println(word + " is not a palindrome.");
        }

        scanner.close();
    }

    public static boolean isPalindrome(String word, int left, int right) {

        // Base case: the pointers have met or crossed
        if (left >= right) {
            return true;
        }

        // Recursive case: compare the outer characters
        if (word.charAt(left) != word.charAt(right)) {
            return false;
        }

        // Move inward and check the remaining characters
        return isPalindrome(word, left + 1, right - 1);
    }
}