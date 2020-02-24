package ChatForm;

import Client.CommandProtocol;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;

public class ClientMain extends Application {
    private static  Controller controller;

    @Override
    public void start(Stage primaryStage) throws Exception{

        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("sample.fxml"));
        controller = loader.getController();
        loader.setController(controller);

        Parent root = loader.load();
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));

        primaryStage.setOnCloseRequest( event -> {
            System.out.println("Stage is closing");
            if (controller!=null)
            {
                controller.OnClose();
            }
            // Save file
            //controller.OnClose();
        });
        primaryStage.setTitle("WebChat");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();



    }


    public static void main(String[] args) {
        launch(args);
        System.out.println(controller==null);
        System.out.println("exiting...");

        //Решаем проблему запущенных потоков
        ThreadGroup rootGroup = Thread.currentThread().getThreadGroup();
        ThreadGroup parent;
        while ((parent = rootGroup.getParent()) != null) {
            rootGroup = parent;
        }

        listThreads(rootGroup, "");
    }


    // Ищем потоки для их остановки
    public static void listThreads(ThreadGroup group, String indent) {
       // System.out.println(indent + "Group[" + group.getName() +
       //         ":" + group.getClass()+"]");
        int nt = group.activeCount();
        Thread[] threads = new Thread[nt*2 + 10]; //nt is not accurate
        nt = group.enumerate(threads, false);

        // List every thread in the group
        for (int i=0; i<nt; i++) {
            Thread t = threads[i];
            String str = String.valueOf(t.getClass());
           if ( str.indexOf("Client.ReadThread")>0 || str.indexOf("Client.WriteThread")>0) {
               java.lang.reflect.Method method;
               try {
                   method = t.getClass().getMethod("Stop", null);
                   method.invoke(t, null);
               } catch (InvocationTargetException | NoSuchMethodException | IllegalAccessException e) {
                   System.out.println(e.getMessage());
               }

           }
          //  System.out.println(indent + "  Thread[" + t.getName()
          //          + ":" + t.getClass() + "]");
        }

        // Recursively list all subgroups
        int ng = group.activeGroupCount();
        ThreadGroup[] groups = new ThreadGroup[ng*2 + 10];
        ng = group.enumerate(groups, false);

        for (int i=0; i<ng; i++) {
            listThreads(groups[i], indent + "  ");
        }
    }

}
