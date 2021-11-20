/**
 * @authour: Omar Khaled Al Haj     20190351
 * @author: Rana Ihab Ahmed Fahmy   20190207
 * @author: ALaa Mahmoud Ebrahim    20190105
 * */

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.*;
import static java.lang.System.exit;

class Parser {
    String commandName;
    String[] args;

    public boolean parse(String input) {
        commandName = input.split(" ")[0];
        input = input.substring(commandName.length());
        if (input.length() > 1 && input.charAt(0) == ' ') {
            input = input.substring(1);
        }
        args = input.split(" ", 0);
        return true;
    }

    public String getCommandName() {
        return commandName;
    }

    public String[] getArgs() {
        return args;
    }
}


public class Terminal {

    Parser parser;
    String currentPath = Paths.get("").toAbsolutePath().toString();

    public Terminal() {
        parser = new Parser();
    }

    public void echo(String[] s) {
        for (int i = 0; i < s.length; i++) {
            System.out.print(s[i] + " ");
        }
        System.out.println();
    }

    public void pwd() {
        System.out.println(currentPath);
    }

    public void ls() {
        File curDir = new File(currentPath);
        String[] arr = curDir.list();
        for (int i = 0; i < arr.length; i++) {
            System.out.println(arr[i]);
        }
    }

    public void cd(String s) {
        if (s.equals("")) {
            currentPath = Paths.get(System.getProperty("user.home")).toAbsolutePath().toString();
            //Should be removed but is left for demonstration
            System.out.println(currentPath);
        } else if (s.equals("..")) {
            int temp = currentPath.split("\\\\")[currentPath.split("\\\\").length - 1].length();
            currentPath = currentPath.substring(0, currentPath.length() - temp - 1);
            //Should be removed but is left for demonstration
            System.out.println(currentPath);
        } else {
            File input = new File(s);
            String tempPath = "";
            if (!input.getAbsolutePath().equals(s)) {
                tempPath = currentPath;
                tempPath += ("\\" + s);
            } else {
                tempPath = s;
            }
            File test = new File(tempPath);
            if (!test.exists()) {
                System.out.println("Error: The path entered does not exist");
            } else {
                currentPath = tempPath;
            }
            System.out.println(currentPath);
        }
    }

    public void lsr() {
        File curDir = new File(currentPath);
        String[] arr = curDir.list();
        int n = arr.length;
        for (int i = n - 1; i >= 0; i--) {
            System.out.println(arr[i]);
        }
    }

    public void mkdir(String args) throws IOException {
        File curDir = new File(currentPath);

        File f = new File(args);
        if (!f.isAbsolute()) {
            f = new File(curDir.getAbsolutePath(), args);
        }
        if (!f.getParentFile().exists()) {
            throw new NoSuchFileException(args, null, "doesn't exist.");
        }
        if (f.exists()) {
            System.out.println("Directory already exists.");
            return;
        }
        boolean isCreated = f.mkdir();
        if (!isCreated) {
            System.out.println("Can't create directory");

        }
    }

    public void rmdir(String s) throws Exception {
        File curDir = new File(currentPath);
        if (s.length ()==0){
            throw new Exception("Wrong, There is no argument.");
        }
        else if (s.equals("*")) {
            if (curDir.isDirectory()) {
                for (File f : curDir.listFiles())
                    if (f.isDirectory()) {
                        if (f.list().length == 0) f.delete();
                    }
            }
        }else {
            File file = new File(s);
            if (!file.isAbsolute()) {
                file = new File(curDir.getAbsolutePath(), s);
            }
            if (file.isDirectory()) {
                String[] files = file.list();
                if (files.length != 0)
                    throw new DirectoryNotEmptyException("Directory is not empty, cannot be deleted");
                else
                    file.delete();
            }
        }
    }

    public void touch(String s) throws IOException {
        File file = new File(s);
        if (!file.isAbsolute()) {
            s = currentPath + File.separator + s;
        }
        Path p = Paths.get(s);
        try {
            Path x = Files.createFile(p);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void cp(String[] args) throws Exception {
        if (args.length != 2) {
            throw new Exception("Wrong number of arguments");
        }
        File input = new File(args[0]);
        if (input.getAbsolutePath() != args[0]) {
            String path = currentPath + File.separator + args[0];
            input = new File(path);
        }
        File output = new File(args[1]);
        if (output.getAbsolutePath() != args[1]) {
            String path = currentPath + File.separator + args[1];
            output = new File(path);
        }
        if (input.isDirectory() || output.isDirectory()) {
            throw new Exception("Arguments should be files not directory");
        }
        if (!input.exists()) {
            throw new Exception("File not found");
        }

        FileChannel source = new FileInputStream(input).getChannel();
        FileChannel destination = new FileOutputStream(output).getChannel();
        destination.transferFrom(source, 0, source.size());
        destination.close();
        source.close();
    }

    public void cp_r(String[] args) {
        if (args.length != 2) {
            System.out.println("Wrong number of arguments");
            return;
        }
        File input = new File(args[0]);
        if (input.getAbsolutePath() != args[0]) {
            String path = currentPath + File.separator + args[0];
            input = new File(path);
        }

        File output = new File(args[1]);
        if (output.getAbsolutePath() != args[1]) {
            String path = currentPath + File.separator + args[1];
            output = new File(path);
        }

        if (!input.exists()) {
            System.out.println("Directory not found");
            return;
        }

        if (!output.exists()) {
            try {
                mkdir(output.getAbsolutePath());
            } catch (Exception e) {
                System.out.println(e.getMessage());
                return;
            }

        }
        if (!input.isDirectory() || !output.isDirectory()) {
            System.out.println("Arguments should be directories not Files");
            return;
        }


        String ar[] = input.list();
        for (String file : ar) {
            File dest = new File(output.getAbsolutePath() + File.separator + file);
            File source = new File(input.getAbsolutePath() + File.separator + file);
            if (source.isDirectory()) {
                cp_r(new String[]{source.getAbsolutePath(), dest.getAbsolutePath()});
            } else {
                try {
                    cp(new String[]{source.getAbsolutePath(), dest.getAbsolutePath()});
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    public void rm(String[] args) {
        if (args.length != 1 || (args.length ==1 && args[0].equals(""))) {
            System.out.println("Wrong number of arguments");
            return;
        }
        File input = new File(args[0]);
        if (!input.getAbsolutePath().equals(args[0])) {
            String path = currentPath + File.separator + args[0];
            input = new File(path);
        }
        if(!input.getParent().equals(currentPath)){
            System.out.println("path should be in current directory");
            return;
        }
        if (input.isDirectory()) {
            System.out.println("Argument should be file not directory");
            return;
        }
        if (!input.exists()) {
            System.out.println("File do not exists in current directory");
            return;
        }
        input.delete();
    }

    public void cat(String[] args) throws Exception {
        if(args.length == 1 && args[0].equals("")){
            throw new Exception("Wrong number of arguments");
        }
        if (args.length != 1 && args.length != 2) {
            throw new Exception("Wrong number of arguments");
        }
        for (int i = 0; i < args.length; i++) {
            File input = new File(args[i]);
            if (input.getAbsolutePath() != args[i]) {
                String path = currentPath + File.separator + args[i];
                input = new File(path);
            }
            if (input.isDirectory()) {
                throw new Exception("Argument should be file not directory");
            }
            BufferedReader read = new BufferedReader(new FileReader(input));
            String line = read.readLine();
            while (line != null) {
                System.out.println(line);
                line = read.readLine();
            }

        }
    }

    public void chooseCommandAction() throws Exception {
        String s = this.parser.getCommandName();
        String[] args = this.parser.getArgs();
        if (s.equals("echo")) {
            if(args.length==1 && args[0].equals("")){
                System.out.println ("Wrong number of arguments");
                return;
            }
            echo(args);
        } else if (s.equals("pwd")) {
            if (args.length==1 && !args[0].equals("")){
                System.out.println ("Wrong, it takes no arguments");
                return;
            }
            pwd ();
        } else if (s.equals("cd")) {
            String temp = "";
            for (int i = 0; i < args.length; i++) {
                if (i != 0) {
                    temp += " ";
                }
                temp += args[i];
            }
            cd(temp);
        } else if (s.equals("ls")) {
            if (args[0].equals("-r")) {
                if (args.length>1){
                    System.out.println ("Wrong, it takes no arguments");
                    return;
                }
                lsr();
            } else {
                if (args.length>0 && !args[0].equals("")){
                    System.out.println ("Wrong, it takes no arguments");
                    return;
                }
                ls();
            }
        } else if (s.equals("mkdir")) {
            if (args.length>0 && args[0].equals("")){
                System.out.println ("Wrong number of arguments");
                return;
            }
            for (int i = 0; i < args.length; i++) {
                mkdir(args[i]);
            }
        } else if (s.equals("rmdir")) {
            if((args.length ==1 && args[0].equals("")) || args.length>1){
                System.out.println ("Wrong number of arguments");
                return;
            }
            try {
                rmdir(args[0]);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (s.equals("touch")) {
            if((args.length ==1 && args[0].equals("")) || args.length>1){
                System.out.println ("Wrong number of arguments");
                return;
            }
            touch(args[0]);
        } else if (s.equals("rm")) {
            rm(args);
        } else if (s.equals("cp")) {
            if (args[0].equals("-r")) {
                String[] arr = new String[args.length - 1];
                for (int i = 1; i < args.length; i++) {
                    arr[i - 1] = args[i];
                }
                cp_r(arr);
            } else {
                try {
                    cp(args);
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }

        } else if (s.equals("cat")) {
            try {
                cat(args);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } else if (s.equals("exit")) {
            exit(0);
        } else {
            System.out.println("Error: Command not found or invalid parameters are entered!");
        }
    }

    public static void main(String[] args){
        try {
            Scanner sc = new Scanner(System.in);
            Terminal m = new Terminal();
            while (true) {
                System.out.print(">");
                String input = "";
                input = sc.nextLine();
                m.parser.parse(input);
                m.chooseCommandAction();
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}