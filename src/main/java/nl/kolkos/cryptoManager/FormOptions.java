package nl.kolkos.cryptoManager;

import java.util.ArrayList;
import java.util.List;

public class FormOptions {
	public List<FormOption> defaultSetHourOptions(){
		List<FormOption> hourOptions = new ArrayList<>();
		
		FormOption hourOption = new FormOption("1", "Last 1 hour");
		hourOptions.add(hourOption);
		hourOption = new FormOption("2", "Last 2 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("3", "Last 3 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("4", "Last 4 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("5", "Last 5 hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("24", "Last 24 Hours");
		hourOptions.add(hourOption);
		hourOption = new FormOption("48", "Last 2 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("168", "Last 7 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("336", "Last 14 days");
		hourOptions.add(hourOption);
		hourOption = new FormOption("720", "Last 30 days");
		hourOptions.add(hourOption);
		
		return hourOptions;
	}
	
	public List<FormOption> defaultSetMinuteOptions(){
		List<FormOption> minuteOptions = new ArrayList<>();
		
		FormOption minuteOption = new FormOption("5", "5 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("10", "10 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("15", "15 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("30", "30 minutes");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("60", "1 hour");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("120", "2 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("300", "5 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("720", "12 hours");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("1440", "1 day");
		minuteOptions.add(minuteOption);
		minuteOption = new FormOption("10080", "1 week");
		minuteOptions.add(minuteOption);
		
		return minuteOptions;
	}
}
