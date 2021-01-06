package edu.kit.informatik;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;
/**
 * @author Ming Ma
 * @version 1.0
 */

public class Postamt {
    private final User[] userList = new User[20];
    private int size;
    private boolean status = false;
    private User logUser = new User();
    private final String[] serList = {"Brief", "Einwurf-Einschreiben", "Einschreiben", "PaketS", "PaketM", "PaketL"};
    private final double[] priceList = {0.70, 1.20, 2.00, 5.00, 6.00, 7.00};

    /**
     * Add a new customer account.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void addCustomer(final String command, final String readCommand) {
        if (!status) {
            boolean ifMatch = Pattern.compile("^add-customer [^;\\n]+;[^;\\n]+;[^;\\n]"
                    + "{4,9};[^;\\n]{4,9};[\\d]{9}$").matcher(readCommand).matches();
            if (ifMatch) {
                String[] paraList = readCommand.split(";");
                Stream<User> userStream = Stream.of(userList);
                long samePerso = userStream.filter((User user) -> user instanceof Kunde).
                        filter((User user) -> ((Kunde) user).getPerso().equals(paraList[4])).count();
                if (samePerso != 0) {
                    Terminal.printError("Personalausweisnummer ist already exist.");
                } else if (paraList[2].equals(paraList[4])) {
                    Terminal.printError("Benuzrname and Personalausweisnummmer cannot be the same.");
                } else {
                    Kunde kunde = new Kunde(paraList, command);
                    userList[size] = kunde;
                    size++;
                }
            } else {
                Terminal.printError("incorrect input format.");
            }
        } else {
            Terminal.printError("there is already a user logged in.");
        }
    }

    /**
     * Add a new mailman account.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void addMailman(final String command, final String readCommand) {
        if (!status) {
            boolean ifMatch = Pattern.compile("^add-mailman [^;\\n]+;[^;\\n]+;"
                    + "[0-9]+;[^;\\n]{4,9}$").matcher(readCommand).matches();
            if (ifMatch) {
                String[] paraList = readCommand.split(";");
                Stream<User> userStreams = Stream.of(userList);
                long sameUser = userStreams.filter((User user) -> user instanceof Callcentermitarbeiter).
                        filter((User user) -> ((Callcentermitarbeiter) user).
                                getPersonalnummer() == Integer.parseInt(paraList[2])).count();
                Stream<User> userStreams1 = Stream.of(userList);
                long sameUser1 = userStreams1.filter((User user) -> user instanceof Postmitarbeiter).
                        filter((User user) -> ((Postmitarbeiter) user).
                                getPersonalnummer() == Integer.parseInt(paraList[2])).count();
                if (sameUser + sameUser1 == 0) {
                    Postmitarbeiter postmitarbeiter = new Postmitarbeiter(paraList, command);
                    userList[size] = postmitarbeiter;
                    size += 1;
                } else {
                    Terminal.printError("personal number already exists.");
                }
            } else {
                Terminal.printError("incorrect input format.");
            }
        } else {
            Terminal.printError("there is already a user logged in.");
        }
    }

    /**
     * Add an agent account.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void addAgent(final String command, final String readCommand) {
        if (!status) {
            boolean ifMatch = Pattern.compile("^add-agent [^;\\n]+;[^;\\n]+;[0-9]+;"
                    + "[^;\\n]{4,9}$").matcher(readCommand).matches();
            if (ifMatch) {
                String[] paraList = readCommand.split(";");
                Stream<User> userStreams = Stream.of(userList);
                long sameUser = userStreams.filter((User user) -> user instanceof Callcentermitarbeiter).
                        filter((User user) -> ((Callcentermitarbeiter) user).
                                getPersonalnummer() == Integer.parseInt(paraList[2])).count();
                Stream<User> userStreams1 = Stream.of(userList);
                long sameUser1 = userStreams1.filter((User user) -> user instanceof Postmitarbeiter).
                        filter((User user) -> ((Postmitarbeiter) user).
                                getPersonalnummer() == Integer.parseInt(paraList[2])).count();
                if (sameUser + sameUser1 == 0) {
                    Callcentermitarbeiter callcentermitarbeiter = new Callcentermitarbeiter(paraList, command);
                    userList[size] = callcentermitarbeiter;
                    size += 1;
                } else {
                    Terminal.printError("personal number already exists.");
                }
            } else {
                Terminal.printError("incorrect input format.");
            }
        } else {
            Terminal.printError("there is already a user logged in.");
        }
    }

    /**
     * Login account.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void authenticate(final String command, final String readCommand) {
        if (!status) {
            boolean ifMatch1 = Pattern.compile("^authenticate [^;\\n]{4,9};"
                    + "[^;\\n]{4,9}$").matcher(readCommand).matches();
            boolean ifMatch2 = Pattern.compile("^authenticate [0-9]+;"
                    + "[^;\\n]{4,9}$").matcher(readCommand).matches();
            if (ifMatch1) {
                String[] paraList = readCommand.split(";");
                for (int i = 0; i < size; i++) {
                    if (userList[i] instanceof Kunde && ((Kunde) userList[i]).getBenutzname().
                            equals(paraList[0].substring(command.length() + 1))
                            && userList[i].getPasswort().equals(paraList[1])) {
                        userList[i].setLogStatus(true);
                        status = true;
                        logUser = userList[i];
                        Terminal.printLine("OK");
                        break;
                    }
                }
                if (!status) {
                    Terminal.printError("the username and/or password entered are not valid.");
                }
            } else if (ifMatch2) {
                String[] paraList = readCommand.split(";");
                for (int i = 0; i < size; i++) {
                    if (userList[i] instanceof Postmitarbeiter
                            && ((Postmitarbeiter) userList[i]).getPersonalnummer()
                            == Integer.parseInt(paraList[0].substring(command.length() + 1))
                            && userList[i].getPasswort().equals(paraList[1])) {
                        userList[i].setLogStatus(true);
                        status = true;
                        logUser = userList[i];
                        Terminal.printLine("OK");
                        break;
                    } else if (userList[i] instanceof Callcentermitarbeiter
                            && ((Callcentermitarbeiter) userList[i]).getPersonalnummer()
                            == Integer.parseInt(paraList[0].substring(command.length() + 1))
                            && userList[i].getPasswort().equals(paraList[1])) {
                        userList[i].setLogStatus(true);
                        status = true;
                        logUser = userList[i];
                        Terminal.printLine("OK");
                        break;
                    }
                }
                if (!status) {
                    Terminal.printError("the username and/or password entered are not valid.");
                }
            } else {
                Terminal.printError("incorrect input format.");
            }
        } else {
            Terminal.printError("there is already a user logged in.");
        }
    }

    /**
     * Logout account.
     * @param readCommand command and parameter.
     */
    public void logout(final String readCommand) {
        Pattern r = Pattern.compile("^logout$");
        Matcher m = r.matcher(readCommand);
        boolean ifMatch = m.matches();
        if (ifMatch) {
            if (!status) {
                Terminal.printError("you have not logged in.");
            } else {
                logUser.setLogStatus(false);
                status = false;
                logUser = null;
                Terminal.printLine("OK");
            }
        } else {
            Terminal.printError("incorrect input format.");
        }
    }

    /**
     * Determine the postal service number.
     * @param string string of service.
     * @return If there is the service, return the service number, if not, return -1.
     */
    private int indexService(final String string) {
        int num = -1;
        for (int i = 0; i < serList.length; i++) {
            if (serList[i].equals(string)) {
                num = i;
                break;
            }
        }
        return num;
    }

    /**
     * Send Mail.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void sendMail(final String command, final String readCommand) {
        if (status) {
            if (logUser instanceof Kunde) {
                boolean ifMatch = Pattern.compile("^send-mail [A-Za-z\\-]+;"
                        + "[^;\\n]{4,9}$").matcher(readCommand).matches();
                if (ifMatch) {
                    String[] pareList = readCommand.split(";");
                    String postService = pareList[0].substring(command.length() + 1);
                    String receiver = pareList[1];
                    if ((indexService(postService)) != -1) {
                        for (int i = 0; i < size; i++) {
                            if (userList[i] instanceof Kunde
                                    && ((Kunde) userList[i]).getBenutzname().equals(receiver)) {
                                ((Kunde) userList[i]).setReceiveList(indexService(postService));
                                ((Kunde) logUser).setSendList(indexService(postService));
                                Terminal.printLine("OK");
                                break;
                            }
                        }
                    } else {
                        Terminal.printError("we don't provide this service.");
                    }
                } else {
                    Terminal.printError("incorrect input format.");
                }
            } else if (logUser instanceof Postmitarbeiter) {
                boolean ifMatch = Pattern.compile("^send-mail [A-Za-z\\-]+;[^;\\n]{4,9};"
                        + "[^;\\n]{4,9}$").matcher(readCommand).matches();
                if (ifMatch) {
                    String[] pareList = readCommand.split(";");
                    String receiver = pareList[1];
                    String sender = pareList[2];
                    int indexService = indexService(pareList[0].substring(command.length() + 1));
                    boolean judgeReceiver = false;
                    boolean judgeSender = false;
                    if (indexService != -1) {
                        for (int i = 0; i < size; i++) {
                            if (userList[i] instanceof Kunde
                                    && ((Kunde) userList[i]).getBenutzname().equals(receiver)) {
                                judgeReceiver = true;
                                for (int j = 0; j < size; j++) {
                                    if (userList[j] instanceof Kunde
                                            && ((Kunde) userList[j]).getBenutzname().equals(sender)) {
                                        ((Kunde) userList[i]).setReceiveList(indexService);
                                        ((Kunde) userList[j]).setSendList(indexService);
                                        Terminal.printLine("OK");
                                        judgeSender = true;
                                        break;
                                    }
                                }
                                if (!judgeSender) {
                                    Terminal.printError("cannot find sender.");
                                }
                                break;
                            }
                        }
                        if (!judgeReceiver) {
                            Terminal.printError("cannot find receiver.");
                        }
                    } else {
                        Terminal.printError("cannot find this service.");
                    }
                } else {
                    Terminal.printError("incorrect input format.");
                }
            } else {
                Terminal.printError("this user does not have permission to send mail.");
            }
        } else {
            Terminal.printError("user has not been authenticated.");
        }
    }

    /**
     * Get all mails from a customer.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void getMail(final String command, final String readCommand) {
        if (status) {
            if (logUser instanceof Kunde) {
                boolean ifMatch = Pattern.compile("^get-mail$").matcher(readCommand).matches();
                if (ifMatch) {
                    int counter = 0;
                    for (int i = 0; i < priceList.length; i++) {
                        counter += ((Kunde) logUser).getReceiveList()[i];
                        ((Kunde) logUser).getReceiveList()[i] = 0;
                    }
                    if (counter == 0) {
                        Terminal.printError("there is no post");
                    } else {
                        Terminal.printLine("OK");
                    }
                } else {
                    Terminal.printError("incorrect input format.");
                }
            } else if (logUser instanceof Postmitarbeiter) {
                boolean ifMatch = Pattern.compile("^get-mail [^;\\n]{4,9}$").matcher(readCommand).matches();
                if (ifMatch) {
                    String receiver = readCommand.substring(command.length() + 1);
                    boolean findUser = false;
                    for (int i = 0; i < size; i++) {
                        if (userList[i] instanceof Kunde && ((Kunde) userList[i]).getBenutzname().equals(receiver)) {
                            int counter = 0;
                            findUser = true;
                            for (int j = 0; j < priceList.length; j++) {
                                counter += ((Kunde) userList[i]).getReceiveList()[j];
                                ((Kunde) userList[i]).getReceiveList()[j] = 0;
                            }
                            if (counter == 0) {
                                Terminal.printError("there is no post");
                            } else {
                                Terminal.printLine("OK");
                            }
                            break;
                        }
                    }
                    if (!findUser) {
                        Terminal.printError("cannot find the receiver.");
                    }
                } else {
                    Terminal.printError("incorrect input format.");
                }
            } else {
                Terminal.printError("you have no permission to get mail.");
            }
        } else {
            Terminal.printError("you have not logged in.");
        }
    }

    /**
     * Show all mails received by a customer.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void listMail(final String command, final String readCommand) {
        if (status) {
            if (logUser instanceof Kunde) {
                boolean ifMatch = Pattern.compile("^list-mail$").matcher(readCommand).matches();
                if (ifMatch) {
                    int counter = 0;
                    for (int i = 0; i < priceList.length; i++) {
                        if (((Kunde) logUser).getReceiveList()[i] != 0) {
                            Terminal.printLine(serList[i] + ";" + ((Kunde) logUser).getReceiveList()[i]);
                            counter += ((Kunde) logUser).getReceiveList()[i];
                        }
                    }
                    if (counter == 0) {
                        Terminal.printLine("OK");
                    }
                } else {
                    Terminal.printError("incorrect input format");
                }
            } else if (logUser instanceof Postmitarbeiter
                    || logUser instanceof Callcentermitarbeiter) {
                boolean ifMatch = Pattern.compile("^list-mail [^;\\n]{4,9}$").matcher(readCommand).matches();
                if (ifMatch) {
                    String receiver = readCommand.substring(command.length() + 1);
                    int counter = 0;
                    for (int i = 0; i < size; i++) {
                        if (userList[i] instanceof Kunde && ((Kunde) userList[i]).getBenutzname().equals(receiver)) {
                            for (int j = 0; j < priceList.length; j++) {
                                if (((Kunde) userList[i]).getReceiveList()[j] != 0) {
                                    Terminal.printLine(serList[j] + ";"
                                            + ((Kunde) userList[i]).getReceiveList()[j]);
                                    counter += ((Kunde) userList[i]).getReceiveList()[j];
                                }
                            }
                            break;
                        }
                    }
                    if (counter == 0) {
                        Terminal.printLine("OK");
                    }
                } else {
                    Terminal.printError("incorrect input format.");
                }
            } else {
                Terminal.printError("you do not have permission to get list.");
            }
        } else {
            Terminal.printError("you have not logged in.");
        }
    }

    /**
     * Show all emails sent by a customer.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void listPrice(final String command, final String readCommand) {
        if (status) {
            if (logUser instanceof Kunde) {
                boolean ifMatch = Pattern.compile("^list-price$").matcher(readCommand).matches();
                if (ifMatch) {
                    int counter = 0;
                    for (int i = 0; i < priceList.length; i++) {
                        if (((Kunde) logUser).getSendList()[i] != 0) {
                            Terminal.printLine(serList[i] + ";" + ((Kunde) logUser).getSendList()[i] + ";"
                                    + String.format("%.2f", priceList[i] * ((Kunde) logUser).getSendList()[i]));
                            counter += ((Kunde) logUser).getSendList()[i];
                        }
                    }
                    if (counter == 0) {
                        Terminal.printLine("OK");
                    }
                } else {
                    Terminal.printError("incorrect input format.");
                }
            } else if (logUser instanceof Postmitarbeiter
                    || logUser instanceof Callcentermitarbeiter) {
                boolean ifMatch = Pattern.compile("^list-price [^;\\n]{4,9}$").matcher(readCommand).matches();
                if (ifMatch) {
                    String receiver = readCommand.substring(command.length() + 1);
                    int counter = 0;
                    for (int i = 0; i < size; i++) {
                        if (userList[i] instanceof Kunde && ((Kunde) userList[i]).getBenutzname().equals(receiver)) {
                            for (int j = 0; j < priceList.length; j++) {
                                if (((Kunde) userList[i]).getSendList()[j] != 0) {
                                    Terminal.printLine(serList[j] + ";"
                                            + ((Kunde) userList[i]).getSendList()[j] + ";"
                                            + String.format("%.2f", priceList[j]
                                            * ((Kunde) userList[i]).getSendList()[j]));
                                    counter += ((Kunde) userList[i]).getSendList()[j];
                                }
                            }
                            break;
                        }
                    }
                    if (counter == 0) {
                        Terminal.printLine("OK");
                    }
                } else {
                    Terminal.printError("incorrect input format.");
                }
            } else {
                Terminal.printError("you do not have permission to get list.");
            }
        } else {
            Terminal.printError("you have not logged in.");
        }
    }

    /**
     * Reset Password.
     * @param command string of command.
     * @param readCommand string of command and parameter.
     */
    public void resetPin(final String command, final String readCommand) {
        boolean ifMatch = Pattern.compile("^reset-pin [^;\\n]{4,9};"
                + "[^;\\n]{4,9}$").matcher(readCommand).matches();
        if (ifMatch) {
            if (status) {
                if (logUser instanceof Callcentermitarbeiter) {
                    String[] paraList = readCommand.split(";");
                    String userName = paraList[0].substring(command.length() + 1);
                    String password = paraList[1];
                    for (int i = 0; i < size; i++) {
                        if (userList[i] instanceof Kunde && ((Kunde) userList[i]).getBenutzname().equals(userName)) {
                            userList[i].setPasswort(password);
                            Terminal.printLine("OK");
                            break;
                        }
                    }
                } else {
                    Terminal.printError("you do not have permission to change the password.");
                }
            } else {
                Terminal.printError("you have not login.");
            }
        } else {
            Terminal.printError("incorrect input format.");
        }
    }
}
