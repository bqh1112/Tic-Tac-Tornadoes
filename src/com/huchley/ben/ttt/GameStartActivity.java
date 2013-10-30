package com.huchley.ben.ttt;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

public class GameStartActivity extends Activity
{
	private Spinner x;
	private Spinner o;
	private String[] choices = {"Player","Very Easy AI", "Easy AI", "Medium AI", "Hard AI"};
	//0 for player, 1 for easy ai, 2 for practice ai
	public static int xType;
	public static int oType;
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.gamestart);
		x = (Spinner) findViewById(R.id.x);
		ArrayAdapter<Object> adapter = new ArrayAdapter<Object>(this, android.R.layout.simple_spinner_item, choices);
		x.setAdapter(adapter);
		o = (Spinner) findViewById(R.id.o);
		ArrayAdapter<Object> adapter2 = new ArrayAdapter<Object>(this, android.R.layout.simple_spinner_item, choices);
		o.setAdapter(adapter2);
		Button go = (Button) findViewById(R.id.go);
		go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				xType = x.getSelectedItemPosition();
				oType = o.getSelectedItemPosition();
				x.setSelection(0);
				o.setSelection(0);
				Intent instr = new Intent(GameStartActivity.this, TTTAndroidActivity.class);
				startActivity(instr);
			}
		});
	}
}
