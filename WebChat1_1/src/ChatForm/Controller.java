package ChatForm;

import ChatForm.Logger.Log;
import Client.CommandProtocol;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    public Label lblError;
    public Pane pnlChat;
    public Pane pnlLogin;
    public Log log=new Log("cli");
    private Player player= new Player(this);
    public boolean DEBUG_MODE=true;
    public CloseClass cClose=new CloseClass();
   public Controller(){
       this.cClose = cClose;
       System.out.println("START....");
       System.out.println(super.getClass().getSuperclass().getName());
   }




    public String PlayerName;


    @FXML
    public TextArea txtMessages;
    @FXML
    public TextField txtSend;
    public TextField edtLogin;
    public TextField edtPassword;
    public Button btnLogin;
    public Button btnReg;
    @FXML
    private Button btnSend;
    private javafx.scene.input.KeyCode KeyCode;


    public void EventRead(CommandProtocol cmd){


        //if (cmd!=null) {
            String CMD = cmd.ReadParam("CMD").trim();


            if (CMD.equals("AUTH")) {
                String Msg = cmd.ReadParam("RESPONSE").trim();
                String Token = cmd.ReadParam("TOKEN").trim();



                AuthComplite(Token, Msg);
            }

           // } else {
                if (cmd.Text.trim().length() > 0) {
                    AddText(cmd.Text.trim() + "\t\n");
                }

           // }


    }

    public void AddText(String sText)
    {
       synchronized ( txtMessages)
       {
         txtMessages.appendText(sText);
       }
    }
    public void LoadLastMessageFromLog(){
        if (player!=null && log!=null) {
           log.renameNikName(player.Name);
           StringBuilder SB = new StringBuilder();
           for (String o : log.GetLastMsg()) {
                log.WriteLog(o);
                SB.append(o+"\t\n");
            }
            AddText(SB.toString());

        }
    }
    public void PrintMessage(String Name, String Text)
    {
        AddText(Name + ": " + Text +"\t\n");
        log.WriteLog(Name + ": " + Text);
    }

    public void btnSend() {
        PrintMessage(player.Name, txtSend.getText());
        player.SendMessage(txtSend.getText());
        txtSend.clear();

    }

    public void btnRename() {
        //PrintMessage(player.Name, txtSend.getText());
        player.Rename(txtSend.getText());
        txtSend.clear();
    }


    public void AuthComplite(String Token, String MSG) {
        player.Token = Token;
        log.WriteSys(player.Token);
        if (Token.length()>0) {
            pnlChat.setVisible(true);
            pnlLogin.setVisible(false);
            LoadLastMessageFromLog();
        }
        else
        {
            lblError.setText(MSG);
        }
        log.WriteSys(MSG.trim());

    }
    public void OnClose(){

        System.out.println("Stage is closing>>>>>>>>");
        player.chat.Close();
        log.Close();
    }
    public void btnLogin() {

        Stage stage = (Stage) btnLogin.getScene().getWindow();
        stage.setOnCloseRequest( event -> { OnClose();


        });



        player.Connect(this,"EventRead" );

        player.Auth(edtLogin.getText(), edtPassword.getText());
/*

        if (player.Auth(edtLogin.getText(), edtPassword.getText()))
        {
           //PnlLoading.SetVisible(true);
            //player.Connect(this,"EventRead" );
        }
        else
        {
            lblError.setText("Не правильный логин или пароль.");
        }*/
    }

    public void SetSendFocus() {
        txtSend.setFocusTraversable(false);
    }


    public void onSendKey (KeyEvent evt) throws IOException {

        if (evt.getCode() == KeyCode.ENTER ){
            btnSend();
            SetSendFocus();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Player player= new Player(this);

    }
}
