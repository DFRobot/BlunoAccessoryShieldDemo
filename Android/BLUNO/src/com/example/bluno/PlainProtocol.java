package com.example.bluno;

public class PlainProtocol {
	
	PlainProtocol(){
		mReceivedframe=new StringBuffer(MaxFrameBufferLenth);
		mSendingFrame=new StringBuffer(MaxFrameBufferLenth);
		  receivedCommand="";
		  sendingCommand="";
		  receivedAddress=0;
		  sendingAddress=0;
		  sendingContent=new int[ContentMaxLenth];
		  receivedContent=new int[ContentMaxLenth];

		  sendingContentLenth=0;
		  isSendingAddress=false;
		  isReceivedAddress=false;
	};
	
	  public StringBuffer mReceivedframe;                           //the frame of the protocol
	  public StringBuffer mSendingFrame;                           //the frame of the protocol
	  private static final int MaxFrameBufferLenth = 500;
	  private static final int ContentMaxLenth = 5;

	  private void sendFrame()                       //send the frame prototype
	  {
		  int index;        //index of the content
		  //send the frame according to the Protocol
		  mSendingFrame.setLength(0);
		  if (isSendingAddress) {
			  mSendingFrame.append('#');
			  mSendingFrame.append(sendingAddress);
		  }
		  mSendingFrame.append('<');
		  mSendingFrame.append(sendingCommand);
		  mSendingFrame.append('>');
		  if (sendingContentLenth!=0) {
		    for (index=0; index<sendingContentLenth-1; index++) {
		    	mSendingFrame.append(sendingContent[index]);
		    	mSendingFrame.append(',');
		    }
		    mSendingFrame.append(sendingContent[index]);
		  }
		  mSendingFrame.append(';');
		  System.out.println(mSendingFrame);
	  };
	//confirm whether the string is a valid number
	 private boolean isNumber(String stringBuf)
	  {
	        try {

	            Integer.parseInt(stringBuf);

	        } catch (NumberFormatException ex) {
	            return false;
	        }

	        return true;
		 
		 
//	    int i;
//	    if (stringBuf.indexOf(0)<'0'||stringBuf.indexOf(0)>'9') {
//	      if (stringBuf.indexOf(0)!='-') {
//	        return false;
//	      }
//	    }
//	    for (i=1; i<stringBuf.length(); i++) {
//	      if (stringBuf.indexOf(i)<'0'||stringBuf.indexOf(i)>'9') {
//	        return false;
//	      }
//	    }
//	    return true;
	  }
	  
	  private boolean parseFrame(String theFrame)    //parse the frame received
	  {  
		  int commaIndex=theFrame.indexOf(',');             //index of the ','
		  int commaIndexLast;                               //index of the last ','
		  int leftAngleBracketIndex=theFrame.indexOf('<');  //index of the '<'
		  int rightAngleBracketIndex=theFrame.indexOf('>'); //index of the '>'
		  int poundSignIndex=theFrame.indexOf('#');         //index of the '#'
		  int contentIndex=0;                               //index of the content
		  String stringBuf;                                 //string buff
		    
		  if (leftAngleBracketIndex!=-1   &&
		      rightAngleBracketIndex!=-1  &&
		      (poundSignIndex==-1 || poundSignIndex<leftAngleBracketIndex)  &&
		      leftAngleBracketIndex < rightAngleBracketIndex  &&
		      (commaIndex==-1 || rightAngleBracketIndex < commaIndex)
		      ) {
		    //frame valid
		    //command Input
		    receivedCommand=theFrame.substring(leftAngleBracketIndex+1, rightAngleBracketIndex);
		    
		    if (commaIndex==-1) {
		      //no comma

		      if (rightAngleBracketIndex+1==theFrame.length()) {
		        //no content
		        receivedContentLenth=0;
		      }
		      else{
		        //one content
		        stringBuf=theFrame.substring(rightAngleBracketIndex+1);
		        
		        //string to number
		        if (isNumber(stringBuf)) {
		          //is a number
		          receivedContent[0]=Integer.parseInt(stringBuf);
		          receivedContentLenth=1;
		        }
		        else{
		          //not a number
		          return false;
		        }
		      }
		    }
		    else{
		      //comma
		      stringBuf=theFrame.substring(rightAngleBracketIndex+1,commaIndex);
		      if (isNumber(stringBuf)) {
		        //is a number
		        //content input
		        receivedContent[contentIndex++]=Integer.parseInt(stringBuf);
		      }
		      else{
		        //not a number
		        return false;
		      }
		      //remember the index of the last ','
		      commaIndexLast=commaIndex;
		      //find the next ','
		      commaIndex=theFrame.indexOf(',',commaIndex+1);
		      while (commaIndex!=-1) {
		        //process until there is no ','
		        stringBuf=theFrame.substring(commaIndexLast+1,commaIndex);
		        if (isNumber(stringBuf)) {
		          //is a number
		          //content input
		          receivedContent[contentIndex++]=Integer.parseInt(stringBuf);
		        }
		        else{
		          //not a number
		          return false;
		        }
		        //remember the index of the last ','
		        commaIndexLast=commaIndex;
		        //find the next ','
		        commaIndex=theFrame.indexOf(',',commaIndex+1);
		      }
		      stringBuf=theFrame.substring(commaIndexLast+1);
		      if (isNumber(stringBuf)) {
		        //is a number
		        //content input
		        receivedContent[contentIndex]=Integer.parseInt(stringBuf);
		        receivedContentLenth=contentIndex+1;
		      }
		      else{
		        //not a number
		        return false;
		      }
		    }
		    
		    if (theFrame.indexOf('#')!=-1){
		      //address available
		      receivedAddress=Integer.parseInt((theFrame.substring(theFrame.indexOf('#')+1, theFrame.indexOf('<'))));
		      isReceivedAddress=true;
		    }
		    else{
		      isReceivedAddress=false;
		    }
		  }
		  else{
		    //frame error
		    return false;
		  }

		  //all done successfully
		  return true;
		}
	  
	  public boolean isSendingAddress,isReceivedAddress; //whether using address mode for received and sending
	  public String receivedCommand,sendingCommand;      //input and output command name
	  public int receivedAddress,sendingAddress;         //input and output adress number
	  public int receivedContent[],sendingContent[];   //input and output content
	  public int receivedContentLenth,sendingContentLenth;   //input and output content lenth



	  //send the frame directly
	  String sendFrame(String theFrame){
		  mSendingFrame.setLength(0);
		  mSendingFrame.append(theFrame);
		  return mSendingFrame.toString();
	  };
	  
	  //send the frame without address, the content should be int only.
	  public String write(String command, int ... contents)
	  {
		  //close the sendingAddress mode
		  isSendingAddress=false;
		  //command output
		  sendingCommand=command;
		  //content lenth
		  sendingContentLenth=contents.length;
		  
		  int indexOfContent=0;
		  for (int content : contents)  
		  {
			  sendingContent[indexOfContent++]=content;
		  }

		  sendFrame();
		  return mSendingFrame.toString();
	  };
	  
	  //send the frame with address, the content should be int only.
	  public String write(int address, String command, int ... contents)
	  {
		  //address
		  sendingAddress=address;
		  //set the sendingAddress mode
		  isSendingAddress=true;
		  //command output
		  sendingCommand=command;
		  //content lenth
		  sendingContentLenth=contents.length;
		  
		  int indexOfContent=0;
		  for (int content : contents)  
		  {
			  sendingContent[indexOfContent++]=content;
		  }

		  sendFrame();
		  return mSendingFrame.toString();
	  };
	  
	  
//	  private boolean isFrameExist=false;

	  boolean available(){
		  int semicolonIndex;
		  boolean isFrameValid=false;

		  if ((semicolonIndex=mReceivedframe.indexOf(";")) != -1) {
			  System.out.print("parseFrame:");
			  System.out.println(mReceivedframe.substring(0, semicolonIndex));

			    if (parseFrame(mReceivedframe.substring(0, semicolonIndex))) {
			    	isFrameValid=true;
			    }
			    else{
			    	System.out.println("<Wrong frame>;");
			    }
			    if(mReceivedframe.length()<=semicolonIndex+1){
			    	mReceivedframe.setLength(0);
			    }
			    else{
			    	mReceivedframe.delete(0, semicolonIndex+1);
			    }
		  }
		  else{
			  if (mReceivedframe.length() >= MaxFrameBufferLenth) {
				  mReceivedframe.setLength(0);
			  }
		  }
		  return isFrameValid;
	  };

}


