package project.Engine;

import project.Engine.Form.Form;
import project.Engine.Http.Http;
import project.CovidTest.TestSite.TestSite;
import project.booking.Booking;
import project.booking.BookingCreator;

import project.booking.HomeBookingCreator;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.function.Predicate;

public class main {
    private static final String API_KEY_FILE_PATH = "path/to/api-key-file/API_KEY.txt";
    public static final String API_KEY;
    public static final String ROOT_URL = "<<PLACEHOLDER FOR API_ROOT_URL>>";
    public static final String USER_ENDPOINT = "<<PLACEHOLDER FOR API ENDPOINT>>";
    public static final boolean LOGGED_IN_AS_PATIENT = true;
    public static final boolean LOGGED_IN_AS_RECEPTIONIST = false;
    public static final boolean LOGGED_IN_AS_HEALTHCARE_WORKER = false;

    // Read the API_KEY from environment at compile-time.
    static {
        File myObj = new File(API_KEY_FILE_PATH);
        String api_key = "";
        try {
            Scanner myReader = new Scanner(myObj);
            api_key = myReader.nextLine();
            myReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("API_KEY can't be opened");
        } finally {
            API_KEY = api_key;
        }
    }

    public static void main(String[] args) {
    }