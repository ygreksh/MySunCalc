package com.example.ygrek.mysuncalc;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by ygrek on 05.01.2018.
 */

public class AstroCalc {
    public static double SunAzimith;
    public static double SunAltitude;
    public static double Deg2Rad = 0.01745329251994329576923690768489;
    public static double Rad2Deg = 1 / Deg2Rad;

    //вычисление Юлианской даты
    public static double getJD(Calendar date){
        int y = date.get(Calendar.YEAR);
        int m = date.get(Calendar.MONTH)+1;
        int d = date.get(Calendar.DAY_OF_MONTH);
        if (m==1 || m==2){
            y = y-1;
            m = m+12;
        }
        int B = 0;
        if (date.after(new GregorianCalendar(1582,Calendar.OCTOBER,15))) {
            int A = y/100;
            B = 2 - A + A/4;
        }
        int C = (int) (365.25*y);
        int D = (int) (30.6001*(m+1));
        double fragmentOfDay = getFragmentaryOfDayByTime(date);
        double JD = B + C + D + d + fragmentOfDay + 1720994.5;

        return JD;
    }
    public static double getMJD(Calendar date){
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH)+1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        long var1 = 10000*year + 100*month + day;
        long var2;
        if (month <= 2){
            month = month+12;
            year = year - 1;
        }
        if (var1 <= 15821004) {
            var2 = -2 + (year + 4716)/4 - 1179;
        } else {
            var2 = year/400 - year/100 + year/4;
        }
        long var3 = 365*year - 679004;
        long MD = var3 + var2 + 306001*(month+1)/1000 + day;
        return MD;
    }
    //получение дробной части дня из времени дна (полдень = 0.5)
    public static double getFragmentaryOfDayByTime(Calendar time){
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int min = time.get(Calendar.MINUTE);
        int sec = time.get(Calendar.SECOND);
        double fragmentaryOfMin = sec/60.0;
        double fragmentaryOfHour = hour + fragmentaryOfMin/60.0;
        double fragmentaryOfDay = fragmentaryOfHour/24.0;
        return fragmentaryOfDay;
    }
    //вычисление дня недели
    // 0 - воскресенье, 1 - понедельник, 2 - вторник и т.д.
    public static int getDayOfWeek(Calendar date){
        double jd = getJD(date);
        double A = (jd + 1.5)/7;
        double remainderOfA = A - Math.floor(A);
        int dayOfWeek = (int) Math.abs(remainderOfA * 7);
        return dayOfWeek;
    }
    //вычисление дробного представления времени в часах
    public static double getFragmentaryHourOfDay(Calendar time){
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int min = time.get(Calendar.MINUTE);
        int sec = time.get(Calendar.SECOND);
        double fragmMin = min + sec/60.0;
        double fragmHour = hour + fragmMin/60;
        return fragmHour;
    }
    //Перевод из дробных часов в нормальные часы минуты и секунды
    public static Calendar getTimaFromFragmentaryHours(double fragmHour){
        int h = (int) Math.floor(fragmHour);
        double fh = fragmHour-(double)h;
        double fm = fh*60;
        int m = (int) Math.floor(fm);
        int s = (int) ((fm - m)*60.0);
        Calendar time = new GregorianCalendar();
        time.set(Calendar.HOUR_OF_DAY,h);
        time.set(Calendar.MINUTE,m);
        time.set(Calendar.SECOND,s);
        return time;
    }
    //перевод в гринвичское или универсальное вермя (GMT или UT)
    public static Calendar convertToUT(Calendar date, int timeZone){
        double fragmHour = AstroCalc.getFragmentaryHourOfDay(date);
        //fragmHour -= date.getTimeZone().getDSTSavings();
        fragmHour -= timeZone;
        Calendar tempDate = AstroCalc.getTimaFromFragmentaryHours(fragmHour);
        Calendar UTdate = new GregorianCalendar();
        UTdate.setTime(date.getTime());
        UTdate.set(Calendar.HOUR_OF_DAY,tempDate.get(Calendar.HOUR_OF_DAY));

        return UTdate;
    }
    //вычисление Католоической Пасхи
    public static Calendar getCatolicEasterDate(Calendar date){
        int y = date.get(Calendar.YEAR);
        int a = y%19;
        int b = y/100;
        int c = y%100;
        int d = b/4;
        int e = b%4;
        int f = (b+8)/25;
        int g = (b-f+1)/3;
        int h = (19*a+b-d-g+15)%30;
        int i = c/4;
        int k = c%4;
        int l = (32+2*e+2*i-h-k)%7;
        int m = (a+11*h+22*l)/451;
        int n = (h+l-7*m+114)/31;
        int p = (h+l-7*m+114)%31;

        int Day = p+1;
        int Month = n-1;

        Calendar easterDate = new GregorianCalendar();
        easterDate.setTime(date.getTime());
        easterDate.set(Calendar.MONTH, Month);
        easterDate.set(Calendar.DAY_OF_MONTH, Day);

        return easterDate;
    }
    //вычисление Православной Пасхи
    public static Calendar getPravoslavEasterDate(Calendar date){
        int y = date.get(Calendar.YEAR);
        int a = y%19;
        int b = y%4;
        int v = y%7;
        int g = (19*a+15)%30;
        int d = (2*b+4*v+6*g+6)%7;
        int Day;
        int Month;
        if((g+d)<9){
            Month = 2;
            Day = 22+g+d;
        }else {
            Month = 3;
            Day = g+d-9;
        }



        Calendar easterDate = new GregorianCalendar();
        easterDate.setTime(date.getTime());
        easterDate.set(Calendar.MONTH, Month);
        easterDate.set(Calendar.DAY_OF_MONTH, Day);
        easterDate.add(Calendar.DATE,13);

        return easterDate;
    }
    //Вычисление положения Солнца
    public static void getSunPosition(double lat, double lng, Calendar date){
        // 0. Перевод времени в UT
        double UT = getFragmentaryHourOfDay(date);
        //double UT = getFragmentaryHourOfDay(convertToUT(date,2));
        // 1. Вычисление модифицированной юлинской даты на начало суток
        /*
        Var1 = 10000 * Year + 100 * Mon + Day
        Если Mon <= 2    Mon = Mon + 12, Year = Year - 1
        Если Var1 <= 15821004, то  Var2 = -2 + (Year + 4716) \ 4 - 1179
        Иначе  Var2 = Year \ 400 - Year \ 100 + Year \ 4
        Var3 = 365 * Year - 679004
        MD = Var3 + Var2 + 306001 * (Mon + 1) \ 10000 + Day
        \ - деление нацело
        */
        int year = date.get(Calendar.YEAR);
        int month = date.get(Calendar.MONTH)+1;
        int day = date.get(Calendar.DAY_OF_MONTH);
        long var1 = 10000*year + 100*month + day;
        long var2;
        if (month <= 2){
            month = month+12;
            year = year - 1;
        }
        if (var1 <= 15821004) {
            var2 = -2 + (year + 4716)/4 - 1179;
        } else {
            var2 = year/400 - year/100 + year/4;
        }
        long var3 = 365*year - 679004;
        long MD = var3 + var2 + 306001*(month+1)/1000 + day;
        //MD = (long) getJD(date);

        // 2. Вычисление местного звездного времени
        /*
        T0 = (MD - 51544.5) / 36525 ‘ мод.юл.дата на начало суток в юлианских столетиях
        a1 = 24110.54841
        a2 = 8640184.812
        a3 = 0.093104
        a4 = 0.0000062
        S0 = a1 + a2 * T0 + a3 * T0 ^ 2 - a4 * T0 ^ 3 ' звездное время в Гринвиче на начало суток в секундах
        Nsec =UT * 3600 ‘ количество секунд, прошедших  от начала суток до момента наблюдения
        UT - всемирное время в часах, момент расчета
        NsecS = Nsec * 366.2422 / 365.2422 ‘ количество звездных секунд
        SG = (S0 + NsecS) /3600 * 15 ‘ гринвическое среднее звездное время в градусах
        ST = SG + Lon ‘ местное звездное время
        Lon – долгота наблюдателя
         */
        double T0 = ((double) MD - 51544.5) / 36525;
        double a1 = 24110.54841;
        double a2 = 8640184.812;
        double a3 = 0.093104;
        double a4 = 0.0000062;
        double S0 = a1 + a2 * T0 + a3 * T0*T0 - a4 * T0*T0*T0;
        double Nsec = UT * 3600;
        double NsecS = Nsec*366.2422/365.2422;
        double SG = (S0 + NsecS) /3600 * 15;
        double ST = SG + lng;

        // 3. Вычисление эклиптических координат Солнца
        /*
        T0 = (MD - 51544.5) / 36525 ‘ мод.юл.дата на начало суток в юлианских столетиях
        UT – время в часах от полуночи даты
        M = 357.528 + 35999.05 * T0 + 0.04107 * UT ‘ средняя аномалия
        L0 = 280.46 + 36000.772 * T0 + 0.04107 * UT
        L = L0 + (1.915 - 0.0048 * T0) * Sin(M) + 0.02 * Sin(2 *M)  ‘ долгота Солнца

        X = Cos(L)  ' вектор
        Y = Sin(L)   '  в эклиптической
        Z = 0         '    системе координат
         */
        double T00 = (MD - 51544.5) / 36525;
        //double UT;
        double M = 357.528 + 35999.05 * T0 + 0.04107 * UT;
        double L0 = 280.46 + 36000.772 * T0 + 0.04107 * UT;
        double L = L0 + (1.915 - 0.0048 * T0) * Math.sin(M) + 0.02 * Math.sin(2*M);
        double X = Math.cos(L);
        double Y = Math.sin(L);
        double Z = 0;


        // 4. Коорлинаты Солнца в прямоугольной экваториалной системе координат
        /*
        Eps = 23.439281   ‘ наклон эклиптики к экватору
        X’ =  X                                    '  вектор
        Y’ = Y * Cos(Eps) - Z * Sin(Eps) '   в экваториальной
        Z’ = Y * Sin(Eps) + Z * Cos(Eps) '    системе координат
         */
        double Eps = 23.449281;
        double Xe = X;
        double Ye = Y * Math.cos(Eps) - Z * Math.sin(Eps);
        double Ze = Y * Math.sin(Eps) + Z * Math.cos(Eps);


        // 5. Экваториальные геоцентрические координаты Солнца
        /*
        tg (Ra) = Y’ /X’
        tg (Dec)= Z’ /  Sqrt(X’ ^ 2 + Y’ ^ 2)
         */
        double TgRa = Ye / Xe;
        double TgDec = Ze / Math.sqrt(Xe*Xe + Ye*Ye);
        double Ra = Math.atan(TgRa);
        double Dec = Math.atan(TgDec);

        // 6. Азимутальные координаты Солнца
        /*
        ST – местное звездное время
        Lat - широта

        Th = ST - Ra  ‘ часовой угол
        Cos (z)  = Sin(Lat) * Sin(Dec) + Cos(Lat) * Cos(Dec) * Cos(Th) ' косинус зенитного угла
        H = 90 –z
        tg (Az) = Sin(Th) * Cos(Dec) * Cos(Lat) / (sin(H) * Sin(Lat) - Sin(Dec))
         */
        double Th = ST - Ra;
        double CosZ = Math.sin(lat) * Math.sin(Dec) + Math.cos(lat) * Math.cos(Dec) * Math.cos(Th);
        double z = Math.acos(CosZ);
        double H = 90 - z;
        double TgAz = Math.sin(Th) * Math.cos(Dec) * Math.cos(lat) / (Math.sin(H) * Math.sin(lat) - Math.sin(Dec));
        double Az = Math.atan(TgAz);
        SunAltitude = H;
        SunAzimith = Az;
    }
}
