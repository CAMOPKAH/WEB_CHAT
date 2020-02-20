package Client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.Properties;

public class CommandProtocol {
    public String Source;
    public String Text;
    public String SrcCfg;
    String cmdBeg="<<cmd";
    String cmdEnd="cmd>>";
    public Properties Prop = new Properties();

    public CommandProtocol(String sourse)
    {
        this.Source=sourse;
    }

    public String ReadParam(String Key) {
        if (Prop!=null) {
            return Prop.getProperty(Key);
        }
        else
        {
            return "";
        }
    }

    public void SetParam(String key, String value) {
        if (Prop==null) {
            Prop = new Properties();
        }
        Prop.setProperty(key, value + "\t\n");
    }

    public void ClearParam() {
        if (Prop!=null) {
            Prop.clear();
        }
        else
        {
            Prop = new Properties();
        }
    }

    public String GenMessage(String Text)
    {
        StringBuilder SB = new StringBuilder();
        if (Prop!=null) {
            String tmpCfg=Prop.toString().trim();
            if (tmpCfg.length()>0)
            {
                SB.append(cmdBeg);
                SB.append("\t\n");

                Enumeration<Object> enumeration = Prop.keys();
                while (enumeration.hasMoreElements()) {
                    String key = (String) enumeration.nextElement();
                    String value =Prop.getProperty(key);
                    SB.append(key);
                    SB.append("=");
                    SB.append(value);
                    SB.append("\t\n");
                }

                //SB.append(tmpCfg);
                SB.append(cmdEnd);
            }

        }
        SB.append(Text);
        return SB.toString();
    }
    public void Parse(String source){
        ClearParam();
        this.Source=source;

        int iBeg = Source.indexOf(cmdBeg);
        int iEnd = Source.indexOf(cmdEnd, iBeg+1);
        if ((iBeg!=-1) && (iEnd!=-1))
        {
            SrcCfg = Source.substring(iBeg+cmdBeg.length(),iEnd-1);
            InputStream stream = new ByteArrayInputStream(SrcCfg.getBytes(StandardCharsets.UTF_8));
            try {
                Prop.load(stream);
            } catch (IOException e) {
                SrcCfg="";
                Prop=null;
            //e.printStackTrace();
            }

            Text = Source.substring(iEnd+cmdEnd.length());
        }
        else
        {
            SrcCfg=null;
            Text=Source;
        }
        System.out.println(SrcCfg);
        System.out.println("____________");
        System.out.println(Text);
    }

}
