package org.example;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import java.time.DayOfWeek;
import java.time.LocalDate;

import de.focus_shift.jollyday.core.HolidayManager;
import de.focus_shift.jollyday.core.ManagerParameters;
import de.focus_shift.jollyday.core.HolidayCalendar;

public class Main {

    //Compute the id of the year given the date
    public static int getYearBS(String date){
        return switch (LocalDate.parse(date).getYear()) {
            case 2011 -> 0;
            case 2012 -> 1;
            default -> -1;
        };

    }

    static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static int getYearDE(String date){
        switch(LocalDate.parse(date, formatter).getYear()){
            case 2022:
                return 0;
            case 2023:
                return 1;
            case 2024:
                return 2;
            default:
                return -1;
        }

    }

    //Compute the month of the year given the date
    public static int getMonth(String date){
        return LocalDate.parse(date).getMonthValue();
    }
    public static int getMonthDE(String date){
        return LocalDateTime.parse(date, formatter).getMonthValue();
    }

    //Compute the day of the week given the date
    public static int getDayOfWeek(String date){
        return LocalDate.parse(date).getDayOfWeek().getValue()%7;
    }
    public static int getDayOfWeekDE(String date){
        return LocalDateTime.parse(date, formatter).getDayOfWeek().getValue()%7;
    }

    //Compute the season given the date
    public static int getSeason(String date){
        LocalDate d = LocalDate.parse(date);
        int month = d.getMonthValue();
        int day = d.getDayOfMonth();

        if ((month == 12 && day >= 21) || month == 1 || month == 2 || (month == 3 && day < 21)) {
            return 1;
        } else if ((month == 3 && day >= 21) || month == 4 || month == 5 || (month == 6 && day < 21)) {
            return 2;
        } else if ((month == 6 && day >= 21) || month == 7 || month == 8 || (month == 9 && day < 23)) {
            return 3;
        } else {
            return 4;
        }
    }

    public static int getSeasonDE(String date){
        LocalDateTime d = LocalDateTime.parse(date, formatter);
        int month = d.getMonthValue();
        int day = d.getDayOfMonth();

        if ((month == 12 && day >= 21) || month == 1 || month == 2 || (month == 3 && day < 21)) {
            return 1;
        } else if ((month == 3 && day >= 21) || month == 4 || month == 5 || (month == 6 && day < 21)) {
            return 2;
        } else if ((month == 6 && day >= 21) || month == 7 || month == 8 || (month == 9 && day < 23)) {
            return 3;
        } else {
            return 4;
        }
    }

    //Determines whether a day is a holiday or not.
    public static HolidayManager hm;
    public static int isHoliday(String date){
        if(hm.isHoliday(LocalDate.parse(date)))
            return 1;
        return 0;
    }
    public static int isHolidayDE(String date){
        if(hm.isHoliday(LocalDateTime.parse(date, formatter).toLocalDate()))
            return 1;
        return 0;
    }

    //Determines whether a day is a working day or not.
    public static int isWorkingDay(String date){
        LocalDate d = LocalDate.parse(date);
        if(d.getDayOfWeek() == DayOfWeek.SUNDAY ||  d.getDayOfWeek() == DayOfWeek.SATURDAY || hm.isHoliday(d))
            return 0;
        return 1;
    }

    public static int isWorkingDayDE(String date){
        LocalDateTime d = LocalDateTime.parse(date, formatter);
        if(d.getDayOfWeek() == DayOfWeek.SUNDAY ||  d.getDayOfWeek() == DayOfWeek.SATURDAY || hm.isHoliday(d.toLocalDate()))
            return 0;
        return 1;
    }

    public static double parseDouble( String n){
        if(n.isEmpty() || n == null)
            return 0.0;
        return Double.parseDouble(n);
    }

    //EPL pipeline for the Bike Sharing dataset
    public static void BikeSharing() {

        //Map the fields of the BikeSharingEvent object to their data type
        Map<String, Object> bikeSharingProperties = new LinkedHashMap<>();
        bikeSharingProperties.put("instant", Integer.class);
        bikeSharingProperties.put("dteday", String.class);
        bikeSharingProperties.put("season", Integer.class);
        bikeSharingProperties.put("yr", Integer.class);
        bikeSharingProperties.put("mnth", Integer.class);
        bikeSharingProperties.put("hr", Integer.class);
        bikeSharingProperties.put("holiday", Integer.class);
        bikeSharingProperties.put("weekday", Integer.class);
        bikeSharingProperties.put("workingday", Integer.class);
        bikeSharingProperties.put("weathersit", Integer.class);
        bikeSharingProperties.put("temp", Double.class);
        bikeSharingProperties.put("atemp", Double.class);
        bikeSharingProperties.put("hum", Double.class);
        bikeSharingProperties.put("windspeed", Double.class);
        bikeSharingProperties.put("casual", Integer.class);
        bikeSharingProperties.put("registered", Integer.class);
        bikeSharingProperties.put("cnt", Integer.class);

        //Create the Esper runtime through the class EventProcessor
        EventProcessor ep = new EventProcessor("prova/data/bike_sharing/hour.csv", "BikeSharingEvent", bikeSharingProperties);

        //Import the needed custom functions
        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getYear",    // The alias you want to use in EPL
                "org.example.Main",         // Fully qualified class name
                "getYearBS"                 // The exact name of the static method
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getMonth",
                "org.example.Main",
                "getMonth"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getDayOfWeek",
                "org.example.Main",
                "getDayOfWeek"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getSeason",
                "org.example.Main",
                "getSeason"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "isHoliday",
                "org.example.Main",
                "isHoliday"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "isWorkingDay",
                "org.example.Main",
                "isWorkingDay"
        );


        StringBuilder queries_pipeline = new  StringBuilder();

        //Step 1: remove column instant
        queries_pipeline.append(
                "INSERT INTO WithoutInstant " +
                        "SELECT dteday , hr, weathersit, temp, hum, windspeed, casual, registered " +
                        "FROM BikeSharingEvent; "
        );

        //Step 2: add column count as casual+registered
        queries_pipeline.append(
                "INSERT INTO WithCount " +
                        "SELECT *, casual+registered as count " +
                        "FROM WithoutInstant; "
        );

        //Step 3: compute Apparent temperature
        String query1 = "INSERT INTO TrueMetrics " +
                "SELECT *,  " +
                "(-8.0 + (47.0)*temp) AS true_temp, " +
                "Math.round(hum*10000)/100 AS true_hum, " +
                "windspeed*67 AS true_windspeed " +
                "FROM WithCount; ";
        String query2 = "INSERT INTO WithATemp " +
                "SELECT dteday , hr, weathersit, casual, registered, count, true_temp as temp, true_hum as hum, true_windspeed as windspeed, " +
                "true_temp+0.33*(true_hum*0.06105*Math.exp((17.27*true_temp)/(237.7+true_temp)))-(0.7*true_windspeed)-4 AS atemp " +
                " FROM TrueMetrics; ";
        queries_pipeline.append(query1);
        queries_pipeline.append(query2);

        //Step 4: transform date into the derived temporal columns, using the american calendar to compute the holidays and working days
        hm = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.UNITED_STATES));
        queries_pipeline.append(
                "INSERT INTO WithTemporal " +
                        "SELECT *, getYear(dteday) as yr, getMonth(dteday) as mnth, getDayOfWeek(dteday) as weekday, getSeason(dteday) as season, isHoliday(dteday) as holiday, isWorkingDay(dteday) as workingday " +
                        "FROM WithATemp; "
        );

        //Step 5: add lag features
        queries_pipeline.append("INSERT INTO WithLag " +
                "SELECT *, " +
                "count - prev(count, 1) as count_lagged_1, registered - prev(registered, 1) as registered_lagged_1, casual - prev(casual, 1) as casual_lagged_1, " +
                "count - prev(count, 24) as count_lagged_24, registered - prev(registered, 24) as registered_lagged_24, casual - prev(casual, 24) as casual_lagged_24, " +
                "count - prev(count, 168) as count_lagged_168, registered - prev(registered, 168) as registered_lagged_168, casual - prev(casual, 168) as casual_lagged_168 " +
                " FROM WithTemporal.win:length(168); ");

        //Step 6: add rolling mean on the last 3, 6, 12 and 24 samples
        queries_pipeline.append( "INSERT INTO WithRM3 " +
                "SELECT *, avg(count) AS count_rm_3, avg(registered) AS registered_rm_3, avg(casual) AS casual_rm_3 " +
                " FROM WithLag.win:length(3); ");

        queries_pipeline.append( "INSERT INTO WithRM6 " +
                "SELECT *, avg(count) AS count_rm_6, avg(registered) AS registered_rm_6, avg(casual) AS casual_rm_6 " +
                " FROM WithRM3.win:length(6); ");

        queries_pipeline.append( "INSERT INTO WithRM12 " +
                "SELECT *, avg(count) AS count_rm_12, avg(registered) AS registered_rm_12, avg(casual) AS casual_rm_12 " +
                " FROM WithRM6.win:length(12); ");

        queries_pipeline.append( "INSERT INTO WithRM24 " +
                "SELECT *, avg(count) AS count_rm_24, avg(registered) AS registered_rm_24, avg(casual) AS casual_rm_24 " +
                " FROM WithRM12.win:length(24); ");

        //Step 7: add EMA with lag 168
        double alpha = 2.0 / (1.0 + 168.0);
        System.out.println("alpha = " + alpha);
        queries_pipeline.append("CREATE VARIABLE double globalEmaCount = 0.0;" +
                "CREATE VARIABLE double globalEmaCasual = 0.0;" +
                "CREATE VARIABLE double globalEmaRegistered = 0.0;");

        queries_pipeline.append( "INSERT INTO FinalStream " +
                "SELECT *, (count*"+alpha+") + (globalEmaCount*(1-"+alpha+")) AS count_ema_168, " +
                "(casual*"+alpha+") + (globalEmaCasual*(1-"+alpha+")) AS casual_ema_168, " +
                "(registered*"+alpha+") + (globalEmaRegistered*(1-"+alpha+")) AS registered_ema_168 " +
                "FROM WithRM24; ");
        queries_pipeline.append("ON WithATemp SET " +
                "globalEmaCount = (count*"+alpha+") + (globalEmaCount*(1-"+alpha+")), " +
                "globalEmaCasual = (casual*"+alpha+") + (globalEmaCasual*(1-"+alpha+")), " +
                "globalEmaRegistered = (registered*"+alpha+") + (globalEmaRegistered*(1-"+alpha+"));");

        //Step 8: select the field to extract from the final stream
        queries_pipeline.append("SELECT * FROM FinalStream;");

        //Compile and deploy the complete pipeline
        ep.compileDeploy(queries_pipeline.toString());

        //Add a listener to collect and print the events of the final stream
        ep.getStatement().addListener((newData, _,_,_) -> {
            if(newData!=null){
                System.out.println(newData[0].getUnderlying());
            }else{
                System.out.println("No data has been found!");
            }
        });

        //Start the stream of events
        ep.startStream("BikeSharingEvent");

    }

    //EPL pipeline for the Dutch Energy dataset
    public static void DutchEnergy() {
        //Map the fields of the DutchEnergy object to their data type
        Map<String, Object> dutchEnergyProperties = new LinkedHashMap<>();
        dutchEnergyProperties.put("MTU", String.class);
        dutchEnergyProperties.put("Biomass", String.class);
        dutchEnergyProperties.put("Fossil Brown coal/Lignite", String.class);
        dutchEnergyProperties.put("Fossil Coal-derived gas", String.class);
        dutchEnergyProperties.put("Fossil Gas", String.class);
        dutchEnergyProperties.put("Fossil Hard coal", String.class);
        dutchEnergyProperties.put("Fossil Oil", String.class);
        dutchEnergyProperties.put("Fossil Oil shale", String.class);
        dutchEnergyProperties.put("Fossil Peat", String.class);
        dutchEnergyProperties.put("Geothermal", String.class);
        dutchEnergyProperties.put("Hydro Pumped Storage", String.class);
        dutchEnergyProperties.put("Hydro Pumped Storage1", String.class);
        dutchEnergyProperties.put("Hydro Run-of-river and poundage", String.class);
        dutchEnergyProperties.put("Hydro Water Reservoir", String.class);
        dutchEnergyProperties.put("Marine", String.class);
        dutchEnergyProperties.put("Nuclear", String.class);
        dutchEnergyProperties.put("Other", String.class);
        dutchEnergyProperties.put("Other renewable", String.class);
        dutchEnergyProperties.put("Solar", String.class);
        dutchEnergyProperties.put("Waste", String.class);
        dutchEnergyProperties.put("Wind Offshore", String.class);
        dutchEnergyProperties.put("Wind Onshore", String.class);

        //Create the Esper runtime through the class EventProcessor
        EventProcessor ep = new EventProcessor("prova/data/dutch-energy/dutch-energy.csv", "DutchEnergyEvent", dutchEnergyProperties);

        //Import the needed custom functions
        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "parseDouble",    // The alias you want to use in EPL
                "org.example.Main",         // Fully qualified class name
                "parseDouble"                 // The exact name of the static method
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getYear",
                "org.example.Main",
                "getYearDE"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getMonth",
                "org.example.Main",
                "getMonthDE"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getDayOfWeek",
                "org.example.Main",
                "getDayOfWeekDE"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "getSeason",
                "org.example.Main",
                "getSeasonDE"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "isHoliday",
                "org.example.Main",
                "isHolidayDE"
        );

        ep.getConfiguration().getCompiler().addPlugInSingleRowFunction(
                "isWorkingDay",
                "org.example.Main",
                "isWorkingDayDE"
        );


        StringBuilder queries_pipeline = new StringBuilder();

        //Step 0: Parse strings to double values
        queries_pipeline.append(
                "INSERT INTO Parsed " +
                        "SELECT MTU, " +
                        "parseDouble(`Biomass`) as Biomass, " +
                        "parseDouble(`Fossil Brown coal/Lignite`) as Fossil_Brown_coal_Lignite, " +
                        "parseDouble(`Fossil Coal-derived gas`) as Fossil_Coal_derived_gas, " +
                        "parseDouble(`Fossil Gas`) as Fossil_Gas, " +
                        "parseDouble(`Fossil Hard coal`) as Fossil_Hard_coal, " +
                        "parseDouble(`Fossil Oil`) as Fossil_Oil, " +
                        "parseDouble(`Fossil Oil shale`) as Fossil_Oil_shale, " +
                        "parseDouble(`Fossil Peat`) as Fossil_Peat, " +
                        "parseDouble(`Geothermal`) as Geothermal, " +
                        "parseDouble(`Hydro Pumped Storage`) as Hydro_Pumped_Storage, " +
                        "parseDouble(`Hydro Pumped Storage1`) as Hydro_Pumped_Storage1, " +
                        "parseDouble(`Hydro Run-of-river and poundage`) as Hydro_Run_of_river_and_poundage, " +
                        "parseDouble(`Hydro Water Reservoir`) as Hydro_Water_Reservoir, " +
                        "parseDouble(`Marine`) as Marine, " +
                        "parseDouble(`Nuclear`) as Nuclear, " +
                        "parseDouble(`Other`) as Other, " +
                        "parseDouble(`Other renewable`) as Other_renewable, " +
                        "parseDouble(`Solar`) as Solar, " +
                        "parseDouble(`Waste`) as Waste, " +
                        "parseDouble(`Wind Offshore`) as Wind_Offshore, " +
                        "parseDouble(`Wind Onshore`) as Wind_Onshore " +
                        "FROM DutchEnergyEvent; "
        );

        //Step 1: transform date into the derived temporal columns, using the dutch calendar to compute the holidays and working days
        hm = HolidayManager.getInstance(ManagerParameters.create(HolidayCalendar.NETHERLANDS));
        queries_pipeline.append(
                "INSERT INTO WithTemporal " +
                        "SELECT *, getYear(MTU) as yr, getMonth(MTU) as mnth, getDayOfWeek(MTU) as weekday, getSeason(MTU) as season, isHoliday(MTU) as holiday, isWorkingDay(MTU) as workingday " +
                        "FROM Parsed; "
        );

        //Step 2: Compute Total energy produced with fossil sources
        queries_pipeline.append(
                "INSERT INTO WithTotalFossil " +
                        "SELECT *, Fossil_Brown_coal_Lignite + Fossil_Coal_derived_gas + Fossil_Gas + Fossil_Hard_coal + Fossil_Oil + Fossil_Oil_shale + Fossil_Peat AS total_fossil " +
                        "FROM WithTemporal;"
        );

        //Step 3: add lag features at lags 1, 96 (1 day) and 672 (1 week) on the field total_fossil
        queries_pipeline.append("INSERT INTO WithLag " +
                "SELECT *, " +
                "total_fossil - prev(total_fossil, 1) as total_fossil_1, " +
                "total_fossil - prev(total_fossil, 96) as total_fossil_96, " +
                "total_fossil - prev(total_fossil, 672) as total_fossil_672 " +
                " FROM WithTotalFossil.win:length(672); ");

        //Step 4: add rolling mean on the last 12, 24, 48 and 96 samples on the field total_fossil
        queries_pipeline.append( "INSERT INTO WithRM12 " +
                "SELECT *, avg(total_fossil) AS total_fossil_rm_12 " +
                " FROM WithLag.win:length(12); ");
        queries_pipeline.append( "INSERT INTO WithRM24 " +
                "SELECT *, avg(total_fossil) AS total_fossil_rm_24 " +
                " FROM WithRM12.win:length(24); ");
        queries_pipeline.append( "INSERT INTO WithRM48 " +
                "SELECT *, avg(total_fossil) AS total_fossil_rm_48 " +
                " FROM WithRM24.win:length(48); ");
        queries_pipeline.append( "INSERT INTO WithRM96 " +
                "SELECT *, avg(total_fossil) AS total_fossil_rm_96 " +
                " FROM WithRM48.win:length(96); ");

        //Step 5: add EMA with lag 672 (1 week)
        double alpha = 2.0 / (1.0 + 672.0);
        System.out.println("alpha = " + alpha);
        queries_pipeline.append("CREATE VARIABLE double globalEmaTotalFossil = 0.0;");

        queries_pipeline.append( "INSERT INTO FinalStream " +
                "SELECT *, (total_fossil*"+alpha+") + (globalEmaTotalFossil*(1-"+alpha+")) AS total_fossil_ema_672 " +
                " FROM WithRM96; ");
        queries_pipeline.append("ON WithRM96 SET globalEmaTotalFossil = (total_fossil*"+alpha+") + (globalEmaTotalFossil*(1-"+alpha+"));");

        //Step 8: select the field to extract from the final stream
        queries_pipeline.append(
                "SELECT * FROM FinalStream"
        );

        //Compile and deploy the complete pipeline
        ep.compileDeploy(queries_pipeline.toString());

        //Add a listener to collect and print the events of the final stream
        ep.getStatement().addListener((newData, _,_,_) -> {
            if(newData!=null){
                System.out.println(newData[0].getUnderlying());
            }else{
                System.out.println("No data has been found!");
            }
        });

        //Start the stream of events
        ep.startStream("DutchEnergyEvent");
    }

    //Change this variable to chose which dataset to use
    static final int dataset = 1;
    static void main() {
        if(dataset==0) {
            BikeSharing();
        }
        if(dataset==1) {
            DutchEnergy();
        }
    }
}