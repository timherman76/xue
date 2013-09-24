package com.MeadowEast.xue;


import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.widget.Toast;

public class AppPreferenceActivity extends PreferenceActivity {
	
	Preference.OnPreferenceChangeListener minMaxListener = new Preference.OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference arg0, Object arg1) {
			boolean result = false;
              try{
            	  int value = Integer.parseInt(arg1.toString());
            	  result = (value >=5 && value <= 100);
              } catch (Exception ex){
            	  
              }
              if ( !result ){
            	  Toast.makeText(AppPreferenceActivity.this, arg1+" "+ "must be between 5 and 100 (inclusive).", Toast.LENGTH_SHORT).show();
              }
              return result;
		}
	};
	
	@Override
	  public void onCreate(Bundle savedInstanceState) {
	      super.onCreate(savedInstanceState);
	      addPreferencesFromResource(R.xml.preferences);
	      
	      findPreference(getString(R.string.pref_key_deck_size_ce)).setOnPreferenceChangeListener(minMaxListener);
	      findPreference(getString(R.string.pref_key_deck_size_ec)).setOnPreferenceChangeListener(minMaxListener);
	  }
}
