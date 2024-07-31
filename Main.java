/*
Dhruvilsinh Mahida
dhruvil-9198
*/


import java.time.LocalDate;
import java.util.Deque;
import java.util.ArrayDeque;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.ArrayList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

interface trains{   //includes functionalities which should be defined by every train for itself
    void book_ticket(Account account, int start, int end);
    void Cancel_ticket(Account account);
}

class Account{
    private String username;
    private String password;

    public Account() {}

    String getUsername(){   //function to get username of user
        return this.username;
    }
    String getpas(){
        return this.password;
    }

    boolean check_password(String s){   //function to check password given by user for login
        return this.password.equals(s);
    }

    void set_name(String s){    //function to set the username entered by user
        this.username=s;
    }

    boolean set_password(String s){     //function to set password entered by user
        /* returns true if the following criteria for password are satisfied:
           1. password is 8 character long
           2. it contains at least one uppercase and one lowercase letter
        */
        if(s.length()<8)
            return false;

        int cap=0,small=0;

        for(int i=0; i<s.length(); i++){
            if(s.charAt(i)>='a' && s.charAt(i)<='z'){   //checks for lowercase letter
                small++;
            }
            if(s.charAt(i)>='A' && s.charAt(i)<='Z'){   // checks for uppercase letter
                cap++;
            }
            if(small>0 && cap>0){
                break;
            }
        }
        if(small>0 && cap>0) {
            this.password = s;  // sets password if all conditions are satisfied
            return true;
        }

        return false;
    }


    ArrayList<ArrayList<String>> bookings = new ArrayList<>();      //arraylist to store details of tickets booked by user

    void view_booking(){    //shows all the tickets booked by user
        if(this.bookings.isEmpty()){
            System.out.println("No bookings found.");
            return;
        }
        for(int i=0; i< this.bookings.size(); i++){
            System.out.println("Passenger " + (i+1) + " details:");
            System.out.println("Name: " + this.bookings.get(i).get(0));
            System.out.println("Age: " + this.bookings.get(i).get(1));
            System.out.println("Train: " + this.bookings.get(i).get(2));
            System.out.println("Status: " + this.bookings.get(i).get(3));
            System.out.println("Coach: " + this.bookings.get(i).get(4));
            System.out.println(("Seat number: " + this.bookings.get(i).get(5)));
            System.out.println("Date of Journey: " + this.bookings.get(i).get(6));
            System.out.println("From: " + this.bookings.get(i).get(7));
            System.out.println("To: " + this.bookings.get(i).get(8));
            System.out.println("Quota: " + this.bookings.get(i).get(9));
            System.out.println();
        }
    }
}


class Train_functionalities extends Account{

    Deque<Account> w_list = new ArrayDeque<>();  // deque to store accounts having a ticket with waiting status.

    Scanner sc = new Scanner(System.in);

    Train_functionalities(){
        super();
    }

    //function to book tickets
    void book_ticket(int date, Account account, int[][] seats, String[] station, String train, int start, int end, int[][] tatkal) throws InputMismatchException, NullPointerException{
        if( start==-1 || end==-1)   //either start or end points to NULL space indicating such station does not exist
                throw new NullPointerException("Station not found...");

        boolean t_book = false;
        int curr_date = LocalDate.now().getDayOfMonth();
        if(date - curr_date == 1){
            System.out.println("""
                    Select quota for booking:
                    1.General quota
                    2. Tatkal quota (extra charges applied)""");

            int t_ch = sc.nextInt();
            if(t_ch == 2)
                t_book = true;
        }

        boolean inter = false;      // variable to indicate whether seats are available or not
        int status = 0;     // variable for waiting/confirmed status. It is also used for allocating seat number

        if(!t_book){//following loop is used to get status
            for (int i = start; i < end; i++) {
                status = Math.max(status, seats[date - 1][i]);
                if (seats[date - 1][i] < 2) {    //check number of seats available for particular date for given station
                    continue;
                }

                // this part of loop executes if seats are not available, i.e., booking is to be done in waiting status
                inter = true;
            }

            //to display number of tickets available or waiting status based on value of inter
            if (!inter)
                System.out.println(2 - status + " tickets available.");
            else {
                System.out.println("No tickets available. Current waiting list: " + (status - 2));
            }
        }
        else{
            for (int i = start; i < end; i++) {
                status = Math.min(status, tatkal[date-1][i]);
                if (tatkal[date - 1][i] < 2) {    //check number of seats available for particular date for given station
                    continue;
                }

                // this part of loop executes if seats are not available, i.e., booking is to be done in waiting status
                inter = true;
            }

            if(!inter)
                System.out.println(2 - status + "tickets available.");
            else
                System.out.println("No tickets available. Current waiting list: " + (status-2));
        }


        System.out.println("Enter number of tickets you want");
        int no_tick = sc.nextInt();
        if(no_tick<=0) {
            System.out.println("Cannot book zero or less tickets");
            return;
        }
        int price = (no_tick*(end-start))*100;

        if(t_book){
            price += 70;
        }

        System.out.println("Total cost is Rs. " + price);
        System.out.println("""
                Please pay amount online to UPI id - railbanking@okrbi (Press D once done)
                Or
                Press E to exit""");

        sc.nextLine();
        String pay = sc.nextLine();
        if(pay.equals("E")){
            System.out.println("Booking cancelled");
        }

        else if (pay.equals("D")) {
            int i=0;
            //following code collects details of passengers and stores them in arraylist
            while(i<no_tick){
                ArrayList<String> details = new ArrayList<>();
                String pas_name;
                int pas_age;
                try{    //checks for incorrect input format
                    System.out.println("Enter name of passenger" + (i + 1) + ":");
                    pas_name = sc.nextLine();
                    System.out.println("Enter age of passenger" + (i + 1) + ":");
                    pas_age = sc.nextInt();
                }
                catch (InputMismatchException e){
                    throw new InputMismatchException("Invalid format of name or age...");
                }

                if(t_book){
                    for (int j = start; j < end; j++) {     //updates available seats for each intermediate stations
                        tatkal[date-1][j]++;
                    }
                }
                else{
                    for (int j = start; j < end; j++) {     //updates available seats for each intermediate stations
                        seats[date - 1][j]++;
                    }
                }

                details.add(pas_name);
                details.add(Integer.toString(pas_age));
                details.add(train);
                String coach;
                if(!inter){ //adding details for confirmed ticket
                    details.add("Confirmed");
                    if(t_book)
                        coach = "D9";
                    else
                        coach = "D" + (status/2 + 1);   //calculates and stores coach number
                    details.add(coach);
                    details.add(Integer.toString(++status));    //calculates and store seat number and updates status for future use
                    System.out.println("Your seat number is " + status + " in " + coach + " coach");
                }
                else{   //adding details for waiting ticket
                    details.add("Waiting " + (++status-2));
                    System.out.println("Your waiting status is " + (status - 2));
                    coach = "-";
                    details.add(coach);     //stores "-" for coach and seat number
                    details.add("-");

                    //adds account details in deque as booked with waiting status
                    if(!w_list.contains(account))
                        this.w_list.add(account);
                }
                //adding common details of each passenger
                details.add(Integer.toString(date));
                details.add(station[start]);
                details.add(station[end]);
                if(t_book)
                    details.add("Tatkal booking");
                else
                    details.add("General booking");

                i++;

                // updating inter variable
                //needs to be updated separately for tatkal
                if(status>=2)
                    inter = true;

                account.bookings.add(details);
                sc.nextLine();
            }

            System.out.println("Booking successful");
        }

        else{   //if any key except "D" or "E" is pressed, this gets executed
            System.out.println("Payment timeout or invalid input received");
            System.out.println("Booking terminated...");
        }
    }


    void Cancel_ticket(Account account,String train_no, int[][] seats, String[] station, int[][] tatkal) throws IndexOutOfBoundsException, InputMismatchException, NullPointerException{
        //shows all the bookings of user
        if(account.bookings.isEmpty()) {
            System.out.println("No tickets booked.");
            return;
        }
        System.out.println("You have booked following tickets");
        account.view_booking();

        while(true){
            //asks user for certain details required for cancellation
            System.out.println("Kindly fill out following details for cancellation");
            System.out.println("Enter date of journey");
            int date;
            try{    //checks for incorrect input format
                date = sc.nextInt();
            }
            catch(InputMismatchException e){
                throw new InputMismatchException("Input mismatch found in cancelling function...");
            }
            sc.nextLine();
            System.out.println("Enter name of passenger");
            String name;
            try{    //checks for incorrect input format
                name = sc.nextLine();
            }
            catch (InputMismatchException e){
                throw new InputMismatchException("Input mismatch found in cancelling function...");
            }

            ArrayList<Integer> record = new ArrayList<>();  //arraylist to store details of all tickets with same name
            try{    //checks for index out of bound problems
                for (int i = 0; i < account.bookings.size(); i++) {
                    if ((account.bookings.get(i).get(2).startsWith(train_no)) && name.equals(account.bookings.get(i).get(0))) {
                        record.add(i);
                    }
                }

                if (record.isEmpty()) {
                    System.out.println("No record found");
                    return;
                }

                int todel = -1;     //variable to store index of ticket to be cancelled from bookings arraylist of user
                for (Integer integer : record) {    //checks for date of journey of all passenger with same name
                    if (Integer.toString(date).equals(account.bookings.get(integer).get(6))) {
                        todel = integer;
                        break;
                    }
                }

                int i;  // to store the current status of ticket to be cancelled
                if (account.bookings.get(todel).get(3).startsWith("Confirmed"))
                    i = 0;
                else {
                    i = Integer.parseInt(account.bookings.get(todel).get(3).substring(8));
                }

                for (Account temp : w_list) {

                    // the following loop visits each ticket in waiting list and updates their waiting status
                    // if waiting status is one, it also confirms its ticket
                    for (int j = 0; j < temp.bookings.size(); j++) {
                        // checks for waiting status, date of journey and quota of booking
                        // if date is same, it enters if part
                        if (temp.bookings.get(j).get(3).startsWith("Waiting") && temp.bookings.get(j).get(6).equals(account.bookings.get(todel).get(6)) &&
                                temp.bookings.get(j).get(9).equals(account.bookings.get(todel).get(9))) {
                            int curr = Integer.parseInt(temp.bookings.get(j).get(3).substring(8));
                            //curr stores the status of current ticket and updates its status
                            if ((curr - 1) == 0 && curr > i) {
                                // if status is 1, it confirms this waiting ticket
                                temp.bookings.get(j).set(3, ("Confirmed"));
                                temp.bookings.get(j).set(4, account.bookings.get(todel).get(4));
                                temp.bookings.get(j).set(5, account.bookings.get(todel).get(5));
                            } else if (curr > i) {
                                //decrements waiting status
                                temp.bookings.get(j).set(3, ("Waiting " + (curr - 1)));
                            }
                        }
                    }
                }

                String board = account.bookings.get(todel).get(7);  //stores boarding station
                String dest = account.bookings.get(todel).get(8);   //stores destination station

                int start = -1, end = -1;   //stores indices of boarding and destination station from station array
                for (int k = 0; k < station.length; k++) {
                    if (station[k].equals(board))
                        start = k;
                    if (station[k].equals(dest))
                        end = k;
                }

                // the following loop increments available seats of all intermediate stations
                if(account.bookings.get(todel).get(4).equals("D9")){
                    for (int k = start; k < end; k++) {
                        tatkal[date - 1][k]--;
                    }
                }
                else{
                    for (int k = start; k < end; k++) {
                        seats[date - 1][k]--;
                    }
                }

                account.bookings.remove(todel);
                System.out.println("Cancellation Successful");

                System.out.println("Enter E to exit, or any other character to cancel another ticket.");
                String in = sc.nextLine();
                if (in.equals("E"))
                    break;
            }
            catch(InputMismatchException e){
                throw new InputMismatchException("Input mismatch found in cancelling function...");
            }
            catch(IndexOutOfBoundsException e){
                throw new IndexOutOfBoundsException("Arraylist index out of bound in cancelling function");
            }
        }
    }
}



class Train extends Train_functionalities implements trains{

    int[][] seats;    //2-D array for storing seats data for each day and between each station
    int[][] tatkal;

    String t_name;
    String t_no;
    String full_name = "";

    //list of stations
    String[] stations;

    Train(String[] station, String name, String num){
        this.stations = station;
        this.t_name = name;
        this.t_no = num;
        this.full_name += (this.t_no + " " + this.t_name);
        this.seats = new int[31][station.length];
        this.tatkal = new int[31][station.length];
        // sets zero indicating no tickets booked yet
        for(int i=0; i<31; i++){
            for(int j=0; j< stations.length; j++) {
                this.seats[i][j] = 0;
                this.tatkal[i][j] = 0;
            }
        }
    }

    //function for booking ticket
    @Override
    public void book_ticket(Account account, int start, int end) {

        System.out.println("Enter date (only dd):");
        int date = sc.nextInt();
        int curr_date = LocalDate.now().getDayOfMonth();

        while(date>31 || date<1 || date < curr_date){
            System.out.println("Please enter a valid date.");
            System.out.println("Enter date (only dd):");
            date = sc.nextInt();
        }
        super.book_ticket(date,account,this.seats,this.stations, full_name, start, end, tatkal);
    }

    //function for cancelling ticket
    @Override
    public void Cancel_ticket(Account account) {
        super.Cancel_ticket(account,t_no, seats, stations, tatkal);
    }
}


public class Main {

    public static void main(String[] args) {

        // to colour output text we define following colour strings
        String Reset = "\u001B[0m";
        String Red = "\u001B[31m";
        String Blue = "\u001B[34m";
        String Yellow = "\u001B[33m";
        String Purple = "\u001B[35m";
        
        ArrayList<Train> trains = new ArrayList<>(); // arraylist for storing all the trains
        Scanner sc = new Scanner(System.in);
        System.out.println(Blue + "____________Welcome to Ticket Booking System____________" + Reset);
        ArrayList<Account> arr = new ArrayList<>();     //arraylist for storing all the users
        
        try{    //checking for errors
            File trainFile = new File("Train_data.txt");    // loads all the trains
            Scanner tF = new Scanner(trainFile);
            while(tF.hasNextLine()){
                String t_det = tF.nextLine();
                String[] data = t_det.split(",");   //retrieves info from file and stores in array
                String[] stat = new String[data.length - 2];
                int i=0;
                for(; i<data.length-2; i++){
                    stat[i] = data[i];
                }
                Train add_tr = new Train(stat, data[i++],  data[i]);
                trains.add(add_tr);
            }
            tF.close();

            File userFile = new File("USERS.txt");  //loads all previous users
            Scanner userFile_read = new Scanner(userFile);

            while(userFile_read.hasNextLine()){
                Account old = new Account();
                String u_name = userFile_read.nextLine();
                String u_pass = userFile_read.nextLine();
                old.set_name(u_name);
                old.set_password(u_pass);
                arr.add(old);
                if(userFile_read.hasNextLine())
                    userFile_read.nextLine();

                File curr_user = new File(u_name + ".txt"); //opens file of particular user to get all its details
                Scanner curr_reader = new Scanner(curr_user);
                
                //reads from file and store in cur_record
                while(curr_reader.hasNextLine()){
                    ArrayList<String> cur_record = new ArrayList<>();
                    String t = curr_reader.nextLine();
                    while(!t.equals("")){
                        cur_record.add(t);
                        if(curr_reader.hasNextLine())
                            t = curr_reader.nextLine();
                        else
                            break;
                    }
                    old.bookings.add(cur_record);   //stores in old arraylist
                }

                //the following code update the seats of train according to tickets previously booked
                for(int i=0; i<old.bookings.size(); i++){
                    String start = old.bookings.get(i).get(7);
                    String end = old.bookings.get(i).get(8);
                    int st = -1, en = -1;
                    int dt = Integer.parseInt(old.bookings.get(i).get(6)) - 1;
                    
                    for(int j=0; j<trains.size(); j++){
                        if(old.bookings.get(i).get(2).startsWith(trains.get(j).t_no)){
                            for(int k=0; k<trains.get(j).stations.length; k++){
                                if(start.equals(trains.get(j).stations[k])){
                                    st = k;
                                }
                                else if(end.equals(trains.get(j).stations[k])){
                                    en = k;
                                }
                                
                                if(st>=0 && en>=0)
                                    break;
                            }
                            if(old.bookings.get(i).get(9).startsWith("General")){
                                for(int k = st; k<=en; k++){
                                    trains.get(j).seats[dt][k]++;
                                }
                            }
                            else{
                                for(int k = st; k<=en; k++){
                                    trains.get(j).tatkal[dt][k]++;
                                }
                            }
                            break;
                        }
                    }
                }
                curr_reader.close();
            }
            userFile_read.close();

            while (true) {
                //displaying menu
                System.out.println(Blue + "Press 1 to login (Enter admin's credential to login as admin)");
                System.out.println("Press 2 to sign up");
                System.out.println("Press 3 to exit" + Reset);
                int choice = sc.nextInt();  //takes input of choice

                if (choice == 3) {
                    //exiting program
                    System.out.println(Blue + "Have a great day!!!" + Reset);
                    break;
                } else if (choice == 1) {
                    sc.nextLine();
                    System.out.println(Blue + "Enter username:" + Reset);
                    String s = sc.nextLine();


                    boolean search = false;     //variable which becomes true if entered details matches with existing account details
                    Account temp = null;    //creating a temporary object of account type
                    for (Account l : arr) {
                        //checking for entered username in arraylist of accounts
                        if (l.getUsername().equals(s)) {
                            search = true;
                            temp = l;
                            break;
                        }
                    }

                    if (!search) {
                        System.out.println(Red + "Username not found...");
                        System.out.println("Please try again..." + Reset);
                        continue;
                    }

                    //checking for password
                    System.out.println(Blue + "Enter password" + Reset);
                    String p = sc.nextLine();

                    if (temp.check_password(p)) {
                        System.out.println(Blue + "Login Successful..." + Reset);
                    } else {
                        System.out.println(Red + "Wrong password..." + Reset);
                        continue;
                    }

                    // if login is successful, we provide sub-menu
                    if(s.equals("admin")){
                        System.out.println(Yellow + "You logged in as admin." + Reset);
                        boolean exit = true;
                        while(exit){
                            System.out.println(Yellow + """
                                    Menu:
                                    1. Add a train
                                    2. Remove a train
                                    3. View trains
                                    4. Exit""" + Reset);

                            int ch = sc.nextInt();
                            sc.nextLine();
                            switch (ch) {
                                case 1:
                                //takes necessary details and create train object
                                    System.out.println(Yellow + "Enter train no." + Reset);
                                    String new_tr_no = sc.nextLine();
                                    boolean tr_found = false;
                                    for(Train tr:trains){   //checks whether train already exists
                                        if(tr.t_no.equals(new_tr_no)){
                                            tr_found = true;
                                            System.out.println("Train already exists.");
                                            break;
                                        }
                                    }
                                    if(tr_found){
                                        break;
                                    }
                                    else {
                                        System.out.println(Yellow + "Enter train name:" + Reset);
                                        String new_tr_name = sc.nextLine();

                                        System.out.println(Yellow + "Enter no. of stops:" + Reset);
                                        int stop = sc.nextInt();
                                        String[] station = new String[stop];
                                        sc.nextLine();
                                        for (int i = 0; i < stop; i++) {
                                            System.out.println(Yellow + "Enter station " + (i+1) + ": " + Reset);
                                            String tmp = sc.nextLine();
                                            tmp = tmp.toLowerCase();
                                            station[i] = tmp;
                                        }

                                        Train train1 = new Train(station, new_tr_name, new_tr_no);
                                        trains.add(train1);
                                        System.out.println(Yellow + "Train added successfully" + Reset);
                                    }
                                    break;
                                case 2:
                                //for removal of specific train
                                    System.out.println(Yellow + "Enter train no. which you want to remove" + Reset);
                                    String tr_no = sc.nextLine();
                                    boolean found = true;
                                    for (Train train : trains) {
                                        if (train.t_no.equals(tr_no)) {
                                            found = false;
                                            trains.remove(train);
                                            System.out.println(Yellow + "Train removed successfully." + Reset);
                                            break;
                                        }
                                    }
                                    if(found){
                                        System.out.println(Red + "No such train found." + Reset);
                                    }

                                    for(Account acc:arr){
                                        for(int i=acc.bookings.size()-1; i>=0; i--){
                                            if(acc.bookings.get(i).get(2).startsWith(tr_no)){
                                                acc.bookings.remove(i);
                                            }
                                        }
                                    }
                                    break;
                                case 3:
                                //shows all the trains
                                    if(trains.isEmpty()){
                                        System.out.println(Red + "No trains added." + Reset);
                                    }
                                    else{
                                        for(Train tr: trains){
                                            System.out.println(Purple + tr.full_name + Reset);
                                        }
                                    }
                                    break;
                                case 4:
                                    exit = false;
                                    break;
                                default:
                                    System.out.println(Red + "Enter a valid choice." + Reset);
                            }
                        }
                    }
                    else {
                        while (true) {
                            //displaying sub-menu
                            System.out.println(Blue + """
                                    Menu:
                                    1. Book Ticket
                                    2. View bookings
                                    3. Cancel ticket
                                    4. Exit""" + Reset);

                            boolean exit = false;   //becomes true if user chooses to exit the program
                            int ch = sc.nextInt();  //takes input of choice
                            sc.nextLine();
                            switch (ch) {
                                case 1:
                                    //calls the booking function of train selected by user
                                    if(trains.isEmpty())
                                        System.out.println(Red + "No trains added. Add a train first to book ticket." + Reset);
                                    else {
                                        System.out.println(Blue + "Routes currently available" + Purple);
                                        //opens file containing name of all stations

                                        for(int i=0; i< trains.size(); i++){
                                            System.out.print(i+1 + ". ");
                                            for(int j=0; j<trains.get(i).stations.length; j++){
                                                if(j == trains.get(i).stations.length-1){
                                                    System.out.println(trains.get(i).stations[j]);
                                                }
                                                else
                                                    System.out.print(trains.get(i).stations[j] + "  -->  ");
                                            }
                                        }


                                        //asks for boarding and destination station
                                        System.out.println(Blue + "Enter boarding station:" + Reset);
                                        String board = sc.nextLine();
                                        board = board.toLowerCase();
                                        System.out.println(Blue + "Enter destination station" + Reset);
                                        String dest = sc.nextLine();
                                        dest = dest.toLowerCase();

                                        //loop for checking trains for given stations
                                        ArrayList<String> available = new ArrayList<>();
                                        int rst = -1, ren = -1;
                                        for (Train train : trains) {
                                            rst = ren = -1;
                                            for (int i = 0; i < train.stations.length; i++) {
                                                if (train.stations[i].equals(board))
                                                    rst = i;
                                                if (train.stations[i].equals(dest))
                                                    ren = i;

                                                if (rst < ren && rst != -1) {
                                                    available.add(train.full_name);
                                                    rst = -1;
                                                    ren = -1;
                                                    break;
                                                }
                                            }
                                        }
                                        
                                        //displays all available trains between selected stations
                                        int st=-1, en=-1;
                                        if (available.isEmpty())
                                            System.out.println(Red + "No trains available between selected stations" + Reset);
                                        else {
                                            System.out.println(Blue + "Select the train:" + Purple);
                                            for (String string : available) {
                                                System.out.println(string);
                                            }


                                            //calling booking function of selected train
                                            boolean called = true;
                                            while (called) {
                                                System.out.println(Blue + "Enter train no.:" + Reset);
                                                String train_ch = sc.nextLine();
                                                for (Train train : trains) {
                                                    if (train.t_no.equals(train_ch)) {
                                                        for (int i = 0; i < train.stations.length; i++) {
                                                            if (train.stations[i].equals(board))
                                                                st = i;
                                                            if (train.stations[i].equals(dest))
                                                                en = i;

                                                            if (st < en && st != -1) {
                                                                break;
                                                            }
                                                        }
                                                        called = false;
                                                        train.book_ticket(temp, st, en);    //calls booking function of selected train
                                                        break;
                                                    }
                                                }
                                                if (called) {
                                                    System.out.println(Red + "Please enter valid train no." + Reset);
                                                }
                                            }

                                            break;  //break for case 1
                                        }
                                    }
                                    break;

                                case 2:
                                    //shows bookings of user currently logged in
                                    temp.view_booking();
                                    break;

                                case 3:
                                    //calls the cancelling function of train selected by user
                                    System.out.println(Blue + "Select the train in which you have ticket:" + Purple);
                                    for (Train train : trains) {
                                        System.out.println(train.full_name);
                                    }
                                    boolean called = true;
                                    while (called) {
                                        System.out.println(Blue + "Enter train no.:" + Reset);
                                        String train_ch2 = sc.nextLine();
                                        for (Train train : trains) {
                                            if (train.t_no.equals(train_ch2)) {
                                                called = false;
                                                train.Cancel_ticket(temp);  //calls cancellation function of selected train
                                                break;
                                            }
                                        }
                                        if (called) {
                                            System.out.println(Red + "Please enter valid train no." + Reset);
                                        }
                                    }
                                    break;

                                case 4:
                                    exit = true;
                                    break;
                            }

                            if (exit) {
                                break;
                            }
                        }
                    }
                } else if (choice == 2) {
                    Account create = new Account();     //creates a new object of account type
                    sc.nextLine();
                    while (true) {
                        System.out.println(Blue + "Set your username:" + Red);
                        String name = sc.nextLine();

                        boolean duplicate = false;  //becomes true if entered username already exists
                        for (Account l : arr) {
                            //checks for duplicate username
                            if (l.getUsername().equals(name)) {
                                duplicate = true;
                                System.out.println(Red + "Username already taken. Please try another username" + Reset);
                                break;
                            }
                        }

                        if (!duplicate) {
                            create.set_name(name);  //sets username
                            arr.add(create);    //adds object in arraylist
                            break;
                        }
                    }


                    System.out.println(Blue + "Set password:");
                    System.out.println("**Should be 8 character long and should contain both capital and small letters" + Reset);
                    String pass = sc.nextLine();

                    boolean b = create.set_password(pass);  //becomes true if all constraints of password are satisfied
                    while (!b) {
                        System.out.println(Red + "Invalid Password" + Reset);
                        System.out.println(Blue + "Please enter a valid password:" + Reset);
                        String valid = sc.nextLine();
                        b = create.set_password(valid);
                    }


                    System.out.println(Blue + "Sign up Successful");
                    System.out.println("Please login to continue" + Reset);
                } else {
                    System.out.println(Red + "Please enter a valid choice" + Reset);
                }
            }
            sc.close();
        }
        catch (IndexOutOfBoundsException e){
            System.out.println(Red + "Got arraylist index out of bound error" + Reset);
        }
        catch (NullPointerException e){
            System.out.println(Red + "Got error due to some uninitialised variable" + Reset);
        }
        catch (InputMismatchException e){
            System.out.println(Red + "Got error  due to invalid input." + Reset);
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        }
        finally {
            System.out.println(Yellow + "Program terminating..." + Reset);
            try {
                /*  
                following code writes all the trains in train text file,
                list of all users in user file
                and bookings of each passenger in file of his/her username
                */
                PrintWriter pw = new PrintWriter("USERS.txt");
                for(Account acc:arr){
                    pw.println(acc.getUsername());
                    pw.println(acc.getpas());
                    pw.println();

                    String fname = acc.getUsername() + ".txt";
                    File fp = new File(fname);
                    PrintWriter pWriter = new PrintWriter(fname);
                    for(int i=0; i< acc.bookings.size(); i++){
                        pWriter.println(acc.bookings.get(i).get(0));
                        pWriter.println(acc.bookings.get(i).get(1));
                        pWriter.println(acc.bookings.get(i).get(2));
                        pWriter.println(acc.bookings.get(i).get(3));
                        pWriter.println(acc.bookings.get(i).get(4));
                        pWriter.println(acc.bookings.get(i).get(5));
                        pWriter.println(acc.bookings.get(i).get(6));
                        pWriter.println(acc.bookings.get(i).get(7));
                        pWriter.println(acc.bookings.get(i).get(8));
                        pWriter.println(acc.bookings.get(i).get(9));
                        pWriter.println();
                    }
                    pWriter.close();
                }
                pw.close();

                PrintWriter trainWriter = new PrintWriter("Train_data.txt");
                for(int i=0; i<trains.size(); i++){
                    for(int j=0; j<trains.get(i).stations.length; j++){
                        trainWriter.print(trains.get(i).stations[j] + ",");
                    }

                    trainWriter.print(trains.get(i).t_name + ",");
                    trainWriter.println(trains.get(i).t_no);
                }
                trainWriter.close();
            } 
            catch (FileNotFoundException e) {
                System.out.println(Red + "User File not created.");
                e.printStackTrace();
            }
        }
    }
}
