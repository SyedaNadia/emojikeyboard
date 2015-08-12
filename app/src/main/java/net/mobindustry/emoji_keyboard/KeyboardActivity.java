package net.mobindustry.emoji_keyboard;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;

import net.mobindustry.emojilib.DpCalculator;
import net.mobindustry.emojilib.EmojiParser;
import net.mobindustry.emojilib.ObservableLinearLayout;
import net.mobindustry.emojilib.Utils;
import net.mobindustry.emojilib.emoji.Emoji;
import net.mobindustry.emojilib.emoji.EmojiKeyboardView;
import net.mobindustry.emojilib.emoji.EmojiPopup;
import net.mobindustry.emojilib.emoji.ImageLoaderHelper;

public class KeyboardActivity extends Activity {

    private Emoji emoji;
    private EmojiParser parser;
    @Nullable
    private EmojiPopup emojiPopup;
    private Emoji.EmojiCallback callback;
    private View.OnClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keyboard);

        ImageLoaderHelper.initImageLoader(this);

        final Button openCloseKeyboard = (Button) findViewById(R.id.open_keyboard_button);
        final EditText emojiResultView = (EditText) findViewById(R.id.emoji_result_view);
        final ImageView stickerResultView = (ImageView) findViewById(R.id.sticker_result_view);

        final ObservableLinearLayout layout = (ObservableLinearLayout) findViewById(R.id.observable_layout);

        callback = new Emoji.EmojiCallback() {
            @Override
            public void loaded() {
                parser = new EmojiParser(emoji);
                openCloseKeyboard.setOnClickListener(listener);
            }
        };

        listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emojiPopup != null) {
                    emojiPopup.dismiss();
                } else {
                    emojiPopup = EmojiPopup.create(KeyboardActivity.this, layout, new EmojiKeyboardView.CallBack() {
                        @Override
                        public void backspaceClicked() {
                            emojiResultView.dispatchKeyEvent(new KeyEvent(0, KeyEvent.KEYCODE_DEL));
                        }

                        @Override
                        public void emojiClicked(long code) {
                            String strEmoji = emoji.toString(code);
                            Editable text = (Editable) emojiResultView.getText();
                            text.append(emoji.replaceEmoji(strEmoji));
                        }

                        @Override
                        public void stickerCLicked(String stickerFilePath) {
                            stickerResultView.setImageURI(Uri.parse(stickerFilePath));
                        }
                    });
                    emojiPopup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            emojiPopup = null;
                        }
                    });
                    assert emojiPopup != null;
                }
            }
        };

        makeEmoji(callback);
    }

    private void makeEmoji(Emoji.EmojiCallback callback) {
        emoji = new Emoji(this, new DpCalculator(Utils.getDensity(this.getResources())));
        emoji.makeEmoji(callback);
    }
}
