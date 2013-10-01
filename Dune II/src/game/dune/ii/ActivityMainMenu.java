package game.dune.ii;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * (c) 2012 Joshua Craymer
 *
 * @author Joshua Craymer
 * @since 02:08:29Z - 25 Oct 2012
 */
public class ActivityMainMenu extends Activity {
	// ===========================================================
	// Constants
	// ===========================================================
	private static final int MUSIC_ID = R.raw.land_of_sand;

	// ===========================================================
	// Fields
	// ===========================================================
	private static AlertDialog  notImplementedDialog;	//The not implemented dialog
	private LinearLayout mainMenuPanel;					//The main menu
	private LinearLayout selectHousePanel;				//The select house panel
	private RelativeLayout houseDescriptionPanel; 		//The house description panel
	private View activePanel; 							//The currently displayed panel
	
	private boolean transitioning = false; //when this is true, the menus are cross-fading. Do do not respond to button clicks
	private MenuSound sound;
	
	// ===========================================================
	// Constructors
	// ===========================================================
	
	// ===========================================================
	// Getters & Setters
	// ===========================================================

	// ===========================================================
	// Methods for/from SuperClass/Interfaces
	// ===========================================================

	/**
	 * Called when the activity is first created. This is where you should do all of your normal static 
	 * set up: create views, bind data to lists, etc. This method also provides you with a Bundle 
	 * containing the activity's previously frozen state, if there was one. 
	 * 
	 * Always followed by onStart().
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_menu_layout);
        
        mainMenuPanel = (LinearLayout) this.findViewById(R.id.main_menu_panel);
        selectHousePanel = (LinearLayout) this.findViewById(R.id.select_house_panel);
        houseDescriptionPanel = (RelativeLayout) this.findViewById(R.id.house_desc_panel);
        
        sound = new MenuSound(this);
        sound.fadeInMusic(MUSIC_ID);
    }

	/**
	* Called when the activity is becoming visible to the user.
	* 
	* Followed by onResume() if the activity comes to the foreground, or onStop() if it becomes hidden.	 
	*/
    @Override
    protected void onStart() {
        super.onStart();

		//
		//If the override scenario is specified, then just skip the menus in the intrest of time
		//
        if(ActivityGame.OVERRIDE_SCENARIO!=null)
        	startCampaign(R.string.atreides);
        
        // The activity is about to become visible. Show the main menu.
        selectHousePanel.setVisibility(View.GONE);
        houseDescriptionPanel.setVisibility(View.GONE);
        mainMenuPanel.setVisibility(View.VISIBLE);
        activePanel = mainMenuPanel;
    }
    
	/**
	 * Called when the activity will start interacting with the user. At this point your activity is at 
	 * the top of the activity stack, with user input going to it. 
	 * 
	 * Always followed by onPause().
	 */
    @Override
    protected void onResume() {
        sound.resume();

    	super.onResume();
        // The activity has become visible (it is now "resumed").
    }

	/**
	 * Called when the system is about to start resuming a previous activity. This is typically used to 
	 * commit unsaved changes to persistent data, stop animations and other things that may be consuming 
	 * CPU, etc. Implementations of this method must be very quick because the next activity will not be 
	 * resumed until this method returns. 
	 * 
	 * Followed by either onResume() if the activity returns back to the front, or onStop() if it becomes 
	 * invisible to the user.
	 */
    @Override
    protected void onPause() {
        sound.pause();

    	super.onPause();
        // Another activity is taking focus (this activity is about to be "paused").
	}

	/**
	 * Called when the activity is no longer visible to the user, because another activity has been resumed 
	 * and is covering this one. This may happen either because a new activity is being started, an existing 
	 * one is being brought in front of this one, or this one is being destroyed.
	 * 
	 * Followed by either onRestart() if this activity is coming back to interact with the user, or onDestroy() 
	 * if this activity is going away.
	 */
    @Override
    protected void onStop() {
        super.onStop();
        // The activity is no longer visible (it is now "stopped")
    }
    
	/**
	 * The final call you receive before your activity is destroyed. This can happen either because the activity 
	 * is finishing (someone called finish() on it, or because the system is temporarily destroying this instance 
	 * of the activity to save space. 
	 * 
	 * You can distinguish between these two scenarios with the isFinishing() method.
	 */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // The activity is about to be destroyed.
    }
    
	/**
	 * Overrides the activity's behavior when the back button is pressed. This allows the user to transition backwards 
	 * through the menus.
	 */
    @Override
	public void onBackPressed() {

    	sound.playGoodClick();
    	
    	if(selectHousePanel.getVisibility()==View.VISIBLE)
    	{
    		showMainMenu();
    		return;
    	}

    	if(houseDescriptionPanel.getVisibility()==View.VISIBLE)
    	{
    		showSelectHouse();
    		return;
    	}
    	
        super.onBackPressed();
    }

	// ===========================================================
	// Non-inherited Methods
	// ===========================================================

	/**
	 * Shows the main menu panel.
	 * 
	 */
    public void showMainMenu() {
    	switchTo(mainMenuPanel);
    }
    
	/**
	 * If the user clicks on the "new game" button, display the chooseHouse dialog
	 * 
	 * @param the button view
	 */
    public void newGameBtn(View view) {
    	//If we are switching menus then do nothing
    	if(transitioning)
    		return;
    	
    	sound.playGoodClick();

    	showSelectHouse();
    }

	/**
	 * If the user clicks on the "load game" button, ... ?
	 * 
	 * @param the button view
	 */
    public void loadBtn(View view) {
    	//If we are switching menus then do nothing
    	if(transitioning)
    		return;

    	sound.playAlert();

    	showNotImplemented();
    }

	/**
	 * If the user clicks on the "quit" button, then quit.
	 * 
	 * @param the button view
	 */
    public void quitBtn(View view) {
    	//If we are switching menus then do nothing
    	if(transitioning)
    		return;

    	sound.playGoodClick();

    	//TODO: Should we even have a quit button?
        finish();
    }

	/**
	 * show the chooseHouse dialog
	 */
    private void showSelectHouse() {
    	switchTo(selectHousePanel);
   	}

	/**
	 * If on the chooseHouse dialog, the user picks the Atredes, show the appropriate description
	 * 
	 * @param the button view
	 */
    public void chooseAtrBtn(View view) {
    	//If we are switching menus then do nothing
    	if(transitioning)
    		return;

    	sound.playGoodClick();

    	showHouseDescription(R.string.atreides);
    }
    
	/**
	 * If on the chooseHouse dialog, the user picks the Ordos, show the appropriate description
	 * 
	 * @param the button view
	 */
    public void chooseOrdBtn(View view) {
    	//If we are switching menus then do nothing
    	if(transitioning)
    		return;

    	sound.playGoodClick();

    	showHouseDescription(R.string.ordos);
    }
    
	/**
	 * If on the chooseHouse dialog, the user picks the Harkonene, show the appropriate description
	 * 
	 * @param the button view
	 */
    public void chooseHarBtn(View view) {
    	//If we are switching menus then do nothing
    	if(transitioning)
    		return;

    	sound.playGoodClick();

    	showHouseDescription(R.string.harkonnen);
    }

	/**
	 * show a description of the specified house
	 * @param atreidesDesc 
	 */
    private void showHouseDescription(final int house) {
    	//
    	// Retrieve the UI views
    	//
    	ImageView mentat_image = (ImageView) findViewById(R.id.mentat_display);
    	TextView mentat_txt = (TextView) findViewById(R.id.mentat_text);
    	
    	//let the mentat_text scroll
    	mentat_txt.setMovementMethod(new ScrollingMovementMethod());
    	
    	//
    	// Set the OK button listener
    	//
    	Button ok_btn = (Button) findViewById(R.id.ok_btn);
    	ok_btn.setOnClickListener(new View.OnClickListener() { public void onClick(View v) {     	
    		//If we are switching menus then do nothing
        	if(transitioning)
        		return;

        	sound.playGoodClick();

        	startCampaign(house);}});
    	
    	//
    	// Fill the views based on the selected house
    	//
    	switch(house) {
    	case R.string.atreides:
    		mentat_txt.setText(R.string.atreides_desc);
    		mentat_image.setImageResource(R.drawable.crest_atr);
    		break;
    	
    	case R.string.harkonnen:
    		mentat_txt.setText(R.string.harkonnen_desc);
    		mentat_image.setImageResource(R.drawable.crest_har);
    		break;
    		
    	default:
    		mentat_txt.setText(R.string.ordos_desc);
    		mentat_image.setImageResource(R.drawable.crest_ord);
    	}

    	switchTo(houseDescriptionPanel);
   	}

	/**
	 * start a new campaign
	 * @param house 
	 */
    private void startCampaign(int house) {
    	
    	//
    	// Close any showing dialogs
    	//
    	selectHousePanel.setVisibility(View.GONE);
    	houseDescriptionPanel.setVisibility(View.GONE);
    	
    	//
    	// Build an intent
    	//
//    	Intent intent = new Intent(this, ActivityMapMenu.class);
    	Intent intent = new Intent(this, ActivityGame.class);
    	
    	//TODO the first scenario files for each house should be here
    	switch(house) {
    	case R.string.atreides:
    		intent.putExtra(Globals.HOUSE_TAG, Globals.HOUSE_ATR);
    		intent.putExtra(Globals.SCENARIO_NUM_TAG, 0);
//    		intent.putExtra(Globals.CAMPAIGN_FILE_TAG, "REGIONA.INI");
//    		intent.putExtra(Globals.BATTLE_NUMBER_TAG, 1);
    		break;
    	
    	case R.string.harkonnen:
    		intent.putExtra(Globals.HOUSE_TAG, Globals.HOUSE_HAR);
    		intent.putExtra(Globals.SCENARIO_NUM_TAG, 0);
//    		intent.putExtra(Globals.CAMPAIGN_FILE_TAG, "REGIONH.INI");
//    		intent.putExtra(Globals.BATTLE_NUMBER_TAG, 1);
    		break;
    		
    	default:
    		intent.putExtra(Globals.HOUSE_TAG, Globals.HOUSE_ORD);
    		intent.putExtra(Globals.SCENARIO_NUM_TAG, 0);
//    		intent.putExtra(Globals.CAMPAIGN_FILE_TAG, "REGIONO.INI");
//    		intent.putExtra(Globals.BATTLE_NUMBER_TAG, 1);
    	}

    	//
    	//get the main window width and height so we know how to setup OpenGL in the next activity
    	//
    	
    	View mainSurface = this.findViewById(R.id.main_menu_image);
    	intent.putExtra(Globals.SCREEN_WIDTH_TAG, mainSurface.getWidth());
    	intent.putExtra(Globals.SCREEN_HEIGHT_TAG, mainSurface.getHeight());
    	
    	
    	
    	//
    	// Show the ActivityMapMenu
    	//
		startActivity(intent);
		overridePendingTransition(R.anim.fadein, R.anim.fadeout);

    	//
    	// re-display the main menu in case the user clicks the back button
    	//
		showMainMenu();
    }
    
	/**
	 * show a dialog stating that specified functionality is not implemented
	 */
    private void showNotImplemented() {
    	//
    	// Build the dialog
    	//
    	if(notImplementedDialog==null) {
    		
    		AlertDialog.Builder builder = new AlertDialog.Builder(this);
    		builder.setMessage(R.string.function_not_implemented)
    			.setTitle(R.string.error)
    			.setNegativeButton(R.string.cancel, null);

    		notImplementedDialog = builder.create();
    	}

    	//
    	// Show the dialog when built
    	//
    	notImplementedDialog.show();
    }
    
    private void switchTo(final View newView)
    {
        final ObjectAnimator fadeIn = ObjectAnimator.ofFloat(newView, "alpha", 1f);
        fadeIn.addListener(new AnimatorListener() {
            @Override 
            public void onAnimationEnd(Animator animation) { 
            	transitioning = false;
            }

			@Override
			public void onAnimationCancel(Animator animation) { }

			@Override
			public void onAnimationRepeat(Animator animation) {	}

			@Override
			public void onAnimationStart(Animator animation) {
				activePanel = newView;
				transitioning = true;
			}
        });
        fadeIn.setDuration(1000);
        
        if(activePanel!=null)
        {
	        ObjectAnimator fadeOut = ObjectAnimator.ofFloat(activePanel, "alpha", 0f);
	        fadeOut.addListener(new AnimatorListener() {
	            @Override 
	            public void onAnimationEnd(Animator animation) {
	    			newView.setVisibility(View.VISIBLE);
	    			newView.setAlpha(0);

	            	activePanel.setVisibility(View.GONE);
	            	fadeIn.start();
	            }
	
				@Override
				public void onAnimationCancel(Animator animation) {
				}
	
				@Override
				public void onAnimationRepeat(Animator animation) {
				}
	
				@Override
				public void onAnimationStart(Animator animation) {
					transitioning = true;
				}
	        });
	        fadeOut.setDuration(1000);
	        fadeOut.start();
        } else {
			newView.setVisibility(View.VISIBLE);
			newView.setAlpha(0);
        	fadeIn.start();
        }
    }
}
