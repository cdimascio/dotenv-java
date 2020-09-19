
package io.github.cdimascio.examples.dotenv;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;


public class Main {

    public static void main(String[] args) {
        Dotenv dotenv = Dotenv.configure().load();

        // Iterate over each environment entry
        // Note: entries in the host environment override entries in .env
        for (DotenvEntry e : dotenv.entries()) {
            System.out.println(e);
        }

        // Retrieve the value of the MY_ENV environment variable
        System.out.println(dotenv.get("MY_ENV"));

        // Retrieve the value of the MY_ENV2 environment variable or return a default value
        System.out.println(dotenv.get("MY_ENV2", "Default Value"));
    }
}
