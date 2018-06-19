/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.revisit.content;

import com.revisit.Model.Node;
import com.revisit.Model.Page;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ContentExtraction {

    /**
     * Calculates the similarity (a number within 0 and 1) between two strings.
     */
    static List<List<String>> documents = new ArrayList<List<String>>();

    public static void main(String[] args) {
        ContentExtraction sim = new ContentExtraction();

    }

    public HashMap<String, ArrayList<Node>> getCotnent(String q2) throws IOException, FileNotFoundException, ClassNotFoundException, SQLException, JSONException {
        HashSet<String> list1 = new HashSet<String>();
        File[] f = retrieveAll();
        // System.out.println("f = size = " + f.length);
        //File f2 = new File("page1.txt");
        for (File file : f) {
            List<String> list = preprocess(file);
            documents.add(list);

        }
        BufferedReader br1 = new BufferedReader(new FileReader(new File("E:\\dataset\\stopwords.txt")));

        String line2 = null;
        String[] stopWords = new String[175];
        Integer index = 0;

        while ((line2 = br1.readLine()) != null) {
            stopWords[index] = line2.trim().toUpperCase();
            index++;

        }
        for (String a1 : q2.split(" ")) {
            if (!a1.isEmpty() && isStringAlpha(a1)) {
                if (!Arrays.asList(stopWords).contains(a1.toUpperCase())) {
                    //list1.add(a1.replaceAll("ing$", "").replaceAll("s$", "").replaceAll("ed$", "").toLowerCase());
                    list1.add(a1.replaceAll("ing$", "").replaceAll("s$", "").replaceAll("ed$", "").toLowerCase());
                }

            }
        }
        HashMap<String, ArrayList<Node>> maptmp = new HashMap<String, ArrayList<Node>>();
        HashMap<String, Integer> map = new HashMap<String, Integer>();

        JSONObject jsonobject = new JSONObject();
        System.out.println("documetns = " + documents.toString());
        String[] qd = q2.split(" ");
        for (File file : f) {
            JSONArray jsonArray = checkHighlights("page1.txt");
            //if (file.getName().equals("page2.txt")) {
            List<String> p = preprocess(file);
            Integer c = 0;
            for (String p2 : p) {
                if (p2.equals("netbeans")) {
                    c++;
                }

            }
            System.out.println("count = " + c);
            for (String p1 : p) {
                Integer i2 = check(p1, jsonArray);
                map.put(p1, i2);
            }
            if (documents != null && documents.size() > 0) {

                TFIDFCalculator calculator = new TFIDFCalculator();
                for (String q : list1) {
                    double tfidf = calculator.tfIdf(p, documents, q);
                    if (tfidf > 0) {
                        Integer h = 0;
                        if (map.get(q) != null) {
                            h = map.get(q);
                        }
                        Double score = (tfidf + h) / 4;

                        if (maptmp.get(q) == null) {
                            ArrayList<Node> tmphash = new ArrayList<Node>();
                            tmphash.add(new Node(file.getName(), score));
                            maptmp.put(q, tmphash);
                        } else {

                            maptmp.get(q).add(new Node(file.getName(), score));
                        }

                    }
                }

            }

        }

        return maptmp;

    }

    public static Integer check(String w, JSONArray array) throws JSONException {

        Integer exist = 0;
        if (array != null) {
            for (int i = 0; i < array.length(); i++) {
                String s = array.getString(i);
                if (w.equals(s)) {
                    exist = 1;

                }

            }
        }

        return exist;
    }

    public static JSONArray checkHighlights(String id) {
        JSONArray jsonArray = null;
        try {
            HightlightRetreiver hr = new HightlightRetreiver();
            //jsonArray = hr.getHighligths(id);
        } catch (JSONException ex) {
            Logger.getLogger(ContentExtraction.class.getName()).log(Level.SEVERE, null, ex);
        }

        return jsonArray;

    }

    public static List<String> stopWords(String line) throws FileNotFoundException, IOException {
        List<String> list = new ArrayList<String>();
        BufferedReader br1 = new BufferedReader(new FileReader(new File("E:\\dataset\\stopwords.txt")));
        HashSet<String> set = new HashSet<String>();
        String line2 = null;
        String[] stopWords = new String[175];
        Integer index = 0;
        StringBuilder sb = new StringBuilder();
        while ((line2 = br1.readLine()) != null) {
            stopWords[index] = line2.trim().toUpperCase();
            index++;

        }

        String[] a = line.toString().split(" ");
        for (String a1 : a) {

            if (!a1.isEmpty() && isStringAlpha(a1)) {
                if (!Arrays.asList(stopWords).contains(a1.toUpperCase())) {
                    list.add(a1.replaceAll("ing$", "").replaceAll("s$", "").replaceAll("ed$", "").toLowerCase());
                    set.add(a1.replaceAll("ing", "").replaceAll("s$", "").toLowerCase());
                }

            }
        }
        return list;
    }

    // get Highlights
    public static void get_highlights(File f) throws FileNotFoundException, IOException, ClassNotFoundException, SQLException {
        BufferedReader br = new BufferedReader(new FileReader(f));
        String line = null;

        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {
            sb.append(line);

        }
        HightlightRetreiver hr = new HightlightRetreiver();
        hr.retrieve(sb.toString(), f.getName());
        System.out.println("sb = " + sb.toString());
        System.out.println("f = " + f.getName());
    }

    public static List<String> preprocess(File f) {
        List<String> list1 = new ArrayList<String>();
        HashSet<String> set = new HashSet<String>();
        String head = null;
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));

            BufferedReader br1 = new BufferedReader(new FileReader(new File("E:\\dataset\\stopwords.txt")));

            String line2 = null;
            String[] stopWords = new String[175];
            Integer index = 0;

            while ((line2 = br1.readLine()) != null) {
                stopWords[index] = line2.trim().toUpperCase();
                index++;

            }

            String line = null;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null) {

                sb.append(line);
                //System.out.println("l = " + line);
                // System.out.println("sb = " + sb.toString());
                String[] a = line.toString().split(" ");
                for (String a1 : a) {

                    if (!a1.isEmpty() && isStringAlpha(a1)) {
                        if (!Arrays.asList(stopWords).contains(a1.toUpperCase())) {
                            list1.add(a1.replaceAll("ing$", "").replaceAll("s$", "").replaceAll("ed$", "").toLowerCase());
                            set.add(a1.replaceAll("ing$", "").replaceAll("s$", "").replaceAll("ed$", "").toLowerCase());
                        }

                    }
                }

            }

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        for (String s : set) {
            //list1.add(s);
        }
        Page p2 = new Page(head, list1);
        return list1;
    }

    public static boolean isStringAlpha(String aString) {
        int charCount = 0;
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        if (aString.length() == 0) {
            return false;//zero length string ain't alpha
        }
        for (int i = 0; i < aString.length(); i++) {
            for (int j = 0; j < alphabet.length(); j++) {
                if (aString.substring(i, i + 1).equals(alphabet.substring(j, j + 1))
                        || aString.substring(i, i + 1).equals(alphabet.substring(j, j + 1).toLowerCase())) {
                    charCount++;
                }
            }
            if (charCount != (i + 1)) {
                //System.out.println("\n**Invalid input! Enter alpha values**\n");
                return false;
            }
        }
        return true;
    }

    public static File[] retrieveAll() {

        String target_dir = "E:\\dataset\\page2\\";
        File dir = new File(target_dir);
        File[] files = dir.listFiles();

        return files;

    }
}
