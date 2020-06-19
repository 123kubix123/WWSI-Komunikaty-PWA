package com.cubix.komunikaty.backend;

import com.gargoylesoftware.htmlunit.HttpMethod;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.WebRequest;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.util.NameValuePair;
import com.google.common.collect.Lists;
import javafx.geometry.Pos;
import org.atmosphere.cpr.BroadcasterListener;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class RssReader {
    private static final RssReader instance = new RssReader();

    private final WebClient client = new WebClient();

    static String url;

    static NameValuePair user;
    static NameValuePair password;
    static NameValuePair login_send;
    
    static ArrayList<Post> Posts;


    private final List<KomunikatListener> listeners = new CopyOnWriteArrayList<KomunikatListener>();

    private RssReader(){
        client.getOptions().setCssEnabled(false);
        client.getOptions().setJavaScriptEnabled(false);
        url = "https://student.wwsi.edu.pl/info";
        Posts = new ArrayList<Post>();
        user = new NameValuePair("login","j_wolski");
        String passwordStr = "";
        try {
            File pass = new File("pass.txt");
            Scanner passReader = new Scanner(pass);
            passwordStr = passReader.nextLine();
            passReader.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        password = new NameValuePair("password",passwordStr);
        login_send = new NameValuePair("login_send","send");

        loadPosts();
        Runnable autorefresh = new Runnable() {
            @Override
            public void run() {
                System.out.println("Refreshing posts...");
                loadPosts();
            }
        };
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.scheduleAtFixedRate(autorefresh,0,30, TimeUnit.SECONDS);
    }

    private  void loadPosts(){
        try{
            WebRequest request = new WebRequest(new URL(url), HttpMethod.POST);
            request.setRequestParameters(new ArrayList<NameValuePair>());
            request.getRequestParameters().add(user);
            request.getRequestParameters().add(password);
            request.getRequestParameters().add(login_send);

            HtmlPage page = client.getPage(request);
            List<HtmlElement> posts = page.getByXPath("//div[@class='news_box']");

            if(!posts.isEmpty()){
                for(HtmlElement post : posts){
                    ArrayList<DomElement> l = Lists.newArrayList(post.getChildElements());
                    String title = l.get(0).asText();
                    String date = l.get(1).asText();
                    String content = l.get(2).asText();
                    //System.out.println(date.asText());
                    Post LoadedPost = new Post(title,date,content);
                    //System.out.println(LoadedPost.getDate());
                    if(!Posts.contains(LoadedPost)) {
                        Posts.add(LoadedPost);
                        broadcast(LoadedPost);
                    }
                }
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void addPost(Post p){
        if(p != null) {
            Posts.add(0,p);
            broadcast(p);
        }
    }

    public static ArrayList<Post> getPosts(){
        return Posts;
    }

    public static RssReader getInstance(){
        return instance;
    }

    public  void register(KomunikatListener listener) {
        listeners.add(listener);
    }

    public  void unregister(KomunikatListener listener) {
        listeners.remove(listener);
    }

    public  void broadcast(final Post p) {
        for (KomunikatListener listener : listeners) {
            listener.Komunikat(p);
        }
    }


}
