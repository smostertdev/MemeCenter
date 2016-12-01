package teamsylvanmatthew.memecenter.Activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.types.GenericMessageEvent;

import java.util.ArrayList;

import teamsylvanmatthew.memecenter.Adapters.MessageAdapter;
import teamsylvanmatthew.memecenter.Models.Message;
import teamsylvanmatthew.memecenter.R;

public class ChatActivity extends AppCompatActivity {
    private String mCurrentUser = "justinfan58503920594859";
    private String mChannel;
    private String mOauth;
    private ListView messageListView;
    private ArrayList<Message> messages;
    private MessageAdapter messageAdapter;
    private PircBotX bot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        mChannel = intent.getStringExtra("channel");
        setTitle("Chat: " + mChannel);

        getCredentials();

        messageListView = (ListView) findViewById(R.id.messageListView);
        messages = new ArrayList<Message>();

        messageAdapter = new MessageAdapter(this, messages, mCurrentUser);
        messageListView.setAdapter(messageAdapter);

        Button sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText msgText = (EditText) findViewById(R.id.messageText);
                if (!msgText.equals("")) {
                    Message sendMessage = new Message(mCurrentUser, msgText.getText().toString());
                    postMessage(sendMessage);
                    bot.sendIRC().message("#" + mChannel, sendMessage.getMessage());
                    msgText.setText("");
                }
            }
        });


        new Thread(new Runnable() {
            public void run() {
                try {
                    Configuration.Builder builder = new Configuration.Builder();
                    builder.addServer("irc.chat.twitch.tv");
                    builder.setName(mCurrentUser);
                    builder.addAutoJoinChannel("#" + mChannel);
                    builder.setMessageDelay(0);
                    builder.setAutoReconnect(false);
                    builder.setAutoSplitMessage(false);
                    builder.setAutoNickChange(false);
                    builder.setOnJoinWhoEnabled(false);

                    if (mOauth != null) {
                        builder.setServerPassword("oauth:" + mOauth);
                        System.out.println("Test 1");
                        builder.setCapEnabled(true);
                        builder.addCapHandler(new EnableCapHandler("twitch.tv/membership"));
                    }


                    builder.addListener(new ListenerAdapter() {
                        @Override
                        public void onGenericMessage(final GenericMessageEvent event) throws Exception {
                            postMessage(new Message(event.getUser().getNick(), event.getMessage()));
                        }

                    });


                    bot = new PircBotX(builder.buildConfiguration());
                    bot.startBot();
/*
                    runOnUiThread(new Runnable() {
                        public void run() {
                            TextView textView = (TextView) findViewById(R.id.connectionText);
                            textView.setVisibility(View.GONE);
                        }
                    });
                    */


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }).start();
/*
        try {
            t.start();
            t.join();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
*/
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();    //Call the back button's method
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getCredentials() {
        SharedPreferences sharedPreferences = getSharedPreferences("memecenter", Context.MODE_PRIVATE);
        if (sharedPreferences.getInt("authenticated", 0) == 1) {
            mCurrentUser = sharedPreferences.getString("username", "justinfan58503920594859");
            mOauth = sharedPreferences.getString("oauth", null);
        }
    }

    private boolean postMessage(Message msg) {
        if (!msg.getMessage().equals("")) {
            messages.add(msg);
            this.runOnUiThread(new Runnable() {
                public void run() {
                    messageAdapter.notifyDataSetChanged();
                }
            });
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        bot.close();
    }

}
