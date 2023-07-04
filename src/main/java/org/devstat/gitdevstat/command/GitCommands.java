/* OpenSource 2023 */
package org.devstat.gitdevstat.command;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

@ShellComponent
public class GitCommands {

    @ShellMethod(key = "hello-world")
    public String helloWorld(@ShellOption(defaultValue = "git") String arg) {
        return "Hello world " + arg;
    }
}
