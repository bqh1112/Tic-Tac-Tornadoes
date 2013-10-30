package com.huchley.ben.ttt;

import android.app.Activity;
//import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class InstructionsActivity extends Activity {
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.instructions);
		Button go = (Button) findViewById(R.id.button1);
		go.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v)
			{
				InstructionsActivity.this.finish();
			}
		});
	}
}
