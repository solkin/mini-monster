package com.tomclaw.minimonster.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;
import com.tomclaw.minimonster.R;
import com.tomclaw.minimonster.dto.Monster;

import java.util.Random;

/**
 * Created by Solkin on 20.08.2015.
 */
public class MonsterImageView extends ImageView {

    private Monster monster;

    private int[] colors;

    private Random random = new Random();
    private PorterDuff.Mode mode = PorterDuff.Mode.SRC_ATOP;

    public MonsterImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        colors = getResources().getIntArray(R.array.avatar_colors);
    }

    public Monster getMonster() {
        return monster;
    }

    public void setMonster(Monster monster) {
        this.monster = monster;

        random.setSeed(hash(monster.getName() + monster.getUrl() + monster.getPassword()));
        int color = colors[random.nextInt(colors.length)];
        Drawable drawable = getResources().getDrawable(R.drawable.monster_background);
        drawable.setColorFilter(color, mode);
        setBackgroundDrawable(drawable);

        setImageResource(R.drawable.ic_glyph_007);
    }

    public int hash(String str) {
        int hash = 0;
        for (int i = 0; i < str.length(); i++) {
            hash = str.charAt(i) + ((hash << 5) - hash);
        }
        return hash;
    }
}
