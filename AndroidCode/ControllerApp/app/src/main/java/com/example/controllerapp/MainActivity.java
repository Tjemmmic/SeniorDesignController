package com.example.controllerapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.controllerapp.R;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements JoystickView.JoystickListener {

    public static final int DEFAULT_SEEK = 90;
    public PrintWriter out;
    public BufferedReader in;
    boolean grabState = false;
    boolean optionOneCheckBox = false;
    boolean optionTwoCheckBox = false;
    TextView output;
    Button mkconn, disconn;
    TextView hostDisplay, portDisplay;
    Thread myNet;
    String messToSend;
    InetAddress serverAddr;
    Socket socket;
    String jslVal;
    String jsrVal;
    String SEPVAL = "<SEP>";
    String gbState = String.valueOf(false);
    String hostnameString;
    String portString;

    SeekBar armSeekOne;
    TextView armOneVal;
    int armOneProgress;
    SeekBar armSeekTwo;
    TextView armTwoVal;
    int armTwoProgress;
    SeekBar armSeekThree;
    TextView armThreeVal;
    int armThreeProgress;

    long time;
    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                // OptionsActivity extras passed back to Main
                if (result.getResultCode() == RESULT_OK) {
                    Intent data = result.getData();
                    optionOneCheckBox = data.getBooleanExtra("optionOneCheckBox", optionOneCheckBox);
                    optionTwoCheckBox = data.getBooleanExtra("optionTwoCheckBox", optionTwoCheckBox);
                    hostnameString = data.getStringExtra("hostname");
                    hostDisplay.setText(hostnameString);
                    portString = data.getStringExtra("port");
                    portDisplay.setText(portString);

                }
            });
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            output.append(msg.getData().getString("msg"));
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        installButton90to90();
        changeGrabText();
        jsrVal = "(0.00,0.00)";
        jslVal = "(0.00,0.00)";
        hostnameString = "12.34.56.789"; // Default IP
        portString = "11888"; // Default port
        output = (TextView) findViewById(R.id.outTerm);
        hostDisplay = (TextView) findViewById(R.id.hostDisplay);
        hostDisplay.setText(hostnameString);
        portDisplay = (TextView) findViewById(R.id.portDisplay);
        portDisplay.setText(portString);
        output.append("\n");
        mkconn = (Button) findViewById(R.id.bConn);
        setSeekBars();
        mkconn.setOnClickListener(v -> {
            new networkTask().execute();
        });


    }

    @Override
    public void onJoystickMoved(float xPercent, float yPercent, int id) {
        TextView leftJoyView = (TextView) findViewById(R.id.leftJoyText);
        TextView rightJoyView = (TextView) findViewById(R.id.rightJoyText);
        switch (id) {
            case R.id.joystickRight:
                Log.d("Right Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
                rightJoyView.setText(String.format("(%.2f,%.2f)", xPercent, yPercent));
                jsrVal = (String.format("(%.2f,%.2f)", xPercent, yPercent));
                break;
            case R.id.joystickLeft:
                Log.d("Left Joystick", "X percent: " + xPercent + " Y percent: " + yPercent);
                leftJoyView.setText(String.format("(%.2f,%.2f)", xPercent, yPercent));
                jslVal = (String.format("(%.2f,%.2f)", xPercent, yPercent));
                break;
        }

    }

    private void setSeekBars() {
        armOneProgress = 90;
        armTwoProgress = 90;
        armThreeProgress = 90;

        armSeekOne = (SeekBar) findViewById(R.id.armSeekOne);
        armSeekOne.setProgress(DEFAULT_SEEK);
        armOneVal = (TextView) findViewById(R.id.armOneVal);
        armSeekOne.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                armOneProgress = armSeekOne.getProgress();
                armOneVal.setText(String.valueOf(armOneProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                armOneProgress = armSeekOne.getProgress();
                armOneVal.setText(String.valueOf(armOneProgress));
            }
        });
        armSeekTwo = (SeekBar) findViewById(R.id.armSeekTwo);
        armSeekTwo.setProgress(DEFAULT_SEEK);
        armTwoVal = (TextView) findViewById(R.id.armTwoVal);
        armSeekTwo.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                armTwoProgress = armSeekTwo.getProgress();
                armTwoVal.setText(String.valueOf(armTwoProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                armTwoProgress = armSeekTwo.getProgress();
                armTwoVal.setText(String.valueOf(armTwoProgress));
            }
        });
        armSeekThree = (SeekBar) findViewById(R.id.armSeekThree);
        armSeekThree.setProgress(DEFAULT_SEEK);
        armThreeVal = (TextView) findViewById(R.id.armThreeVal);
        armSeekThree.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                armThreeProgress = armSeekThree.getProgress();
                armThreeVal.setText(String.valueOf(armThreeProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                armThreeProgress = armSeekThree.getProgress();
                armThreeVal.setText(String.valueOf(armThreeProgress));
            }
        });
    }

    private void changeGrabText() {
        TextView grabTextView = (TextView) findViewById(R.id.grabStateText);
        Button buttonGrabChange = (Button) findViewById(R.id.buttonChangeGrabState);
        buttonGrabChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                grabState = (!grabState);
                gbState = String.valueOf(grabState);
                if (grabState) {
                    grabTextView.setText("Grab");
                } else {
                    grabTextView.setText("No Grab");
                }
            }
        });
    }

    private void installButton90to90() {
        final AllAngleExpandableButton button = findViewById(R.id.button_expandable_90_90);
        final List<ButtonData> buttonDatas = new ArrayList<>();
        int[] drawable = {R.drawable.plus, R.drawable.logs, R.drawable.cog, R.drawable.heart};
        int[] color = {R.color.blue, R.color.red, R.color.green, R.color.yellow};
        for (int i = 0; i < 4; i++) {
            ButtonData buttonData;
            if (i == 0) {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 15);
            } else {
                buttonData = ButtonData.buildIconButton(this, drawable[i], 0);
            }
            buttonData.setBackgroundColorId(this, color[i]);
            buttonDatas.add(buttonData);
        }
        button.setButtonDatas(buttonDatas);
        setListener(button);
    }

    private void setListener(AllAngleExpandableButton button) {
        button.setButtonEventListener(new ButtonEventListener() {
            @Override
            public void onButtonClicked(int index) {
                button90EventCall(index);
            }


            @Override
            public void onExpand() {
            }

            @Override
            public void onCollapse() {
            }
        });
    }

    private void button90EventCall(int index) {
        switch (index) {
            case 1: // LOG ACTIVITY
                showToast("Logs Opened");
                Intent switchLogIntent = new Intent(this, LogActivity.class);
                startActivity(switchLogIntent);
                break;
            case 2: // OPTIONS ACTIVITY
                showToast("Options Opened");
                Intent switchOptionsIntent = new Intent(this, OptionsActivity.class);
                openYourActivity(switchOptionsIntent);

                break;
            case 3: // ACTIVITY UNDER CONSTRUCTION
                showToast("NULL Activity");
                break;
            default:
                showToast("Error: Index Does Not Exist...");
        }
    }

    public void openYourActivity(Intent intent) {
        intent.putExtra("optionOneState", optionOneCheckBox);
        intent.putExtra("optionTwoState", optionTwoCheckBox);
        intent.putExtra("hostnameText", String.valueOf(hostDisplay.getText()));
        intent.putExtra("portText", String.valueOf(portDisplay.getText()));
        launchSomeActivity.launch(intent);
    }

    private void showToast(String toast) {
        Toast.makeText(this, toast, Toast.LENGTH_SHORT).show();
    }

    public class networkTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] objects) {
            doNetwork stuff = new doNetwork();
            myNet = new Thread(stuff);
            myNet.start();
            return null;
        }

        class doNetwork implements Runnable {
            String prevMess = "Init";
            String messHalf;

            public void run() {
                int p = Integer.parseInt(portDisplay.getText().toString());
                boolean currentSession = false;
                disconn = (Button) findViewById(R.id.disconn);
                String h = hostDisplay.getText().toString();
                Log.d("Host Connection", h);
                try {
                    serverAddr = InetAddress.getByName(h);
                    socket = new Socket(serverAddr, p);
                    //String message = "Connection Request: BOE1234";
                    out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    currentSession = false;
                    disconn.setOnClickListener(v -> {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        out.close();
                        try {
                            socket.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return;
                    });
                    try {
                        while (!currentSession) {
                            time = System.currentTimeMillis();
                            messHalf = "RJS:" + jsrVal + SEPVAL + "LJS:" + jslVal + SEPVAL + "AOP:" + armOneProgress + SEPVAL + "ATP:" + armTwoProgress + SEPVAL + "A3P:" + armThreeProgress + SEPVAL + "GBS:" + gbState + SEPVAL;
                            if (!(messHalf.equals(prevMess))) {
                                messToSend = messHalf + time;
                                out.println(messToSend);
                                prevMess = messHalf;
                                String str = in.readLine();
                                Log.d("Received", str);
                            } else {
                            }

                            if (messToSend.equals("connEndTimeRes")) {
                                currentSession = true;
                            }
                        }
                    } catch (Exception e) {
                        Log.d("Error", "Abrupt Session End");
                    } finally {
                        in.close();
                        out.close();
                        socket.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}