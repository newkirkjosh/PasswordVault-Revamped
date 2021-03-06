package org.research.thevault;

import android.provider.BaseColumns;
import org.research.chatclient.R;

public interface Constants extends BaseColumns {
	
	public static final String PASS_TABLE_NAME = "password" ;
	public static final String SITE_TABLE_NAME = "sites" ;
	
	// Columns in the Events database
	public static final String PWORD = "password";
	public static final String URL = "url";
	public static final String UNAME = "username";
	
	//chat client fields
	public static final String MESSAGE_TABLE_NAME = "Messages";
	
	// Columns in the Events database
	public static final String SENDER = "sender";
	public static final String RECIPIENT = "recipient";
	public static final String OTHER_MEMBER = "other_member";
	public static final String MESSAGE = "message";
	public static final String TIMESTAMP = "timestamp";
	public static final String CONVERSATION_ID = "conversation_id";
}

