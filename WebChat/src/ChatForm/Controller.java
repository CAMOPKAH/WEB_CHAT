package ChatForm;

import Client.CommandProtocol;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;


public class Controller implements Initializable {
    public Label lblError;
    public Pane pnlChat;
    public Pane pnlLogin;
    private Player player= new Player();
    public boolean DEBUG_MODE=true;


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
                    txtMessages.appendText(cmd.Text + "\t\n");
                }

           // }

    }
    public void PrintMessage(String Name, String Text)
    {
        txtMessages.appendText(Name + ": " + Text +"\t\n");
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

    public void OnClose()
    {
        //Player.chat.Close();
    }
    public void AuthComplite(String Token, String MSG) {
        player.Token = Token;
        System.out.println(player.Token);
        if (Token.length()>0) {
            pnlChat.setVisible(true);
            pnlLogin.setVisible(false);
        }
        else
        {
            lblError.setText(MSG);
        }
        System.out.println(MSG.trim());

    }
    public void btnLogin() {

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
        Player player= new Player();
    }
}
