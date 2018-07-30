package com.example.ygrek.mysuncalc;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Created by ygrek on 05.01.2018.
 */

public class AstroCalc {

    public static double SunAzimuth;
    public static double SunAltitude;

    public static final double Deg2Rad = 0.01745329251994329576923690768489;
    public static final double Rad2Deg = 1 / Deg2Rad;

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
        double JD = B + C + D + d + 1720994.5;

        return JD;
    }
    //вычисление дробной юлианской даты с учетом времени дня
    public static double fragmentarygetJD(Calendar date){
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
    //вычисление дробного представления времени в часах (0.0-24.0)
    public static double getFragmentaryHourOfDay(Calendar time){
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int min = time.get(Calendar.MINUTE);
        int sec = time.get(Calendar.SECOND);
        double fragmMin = min + sec/60.0;
        double fragmHour = hour + fragmMin/60;
        return fragmHour;
    }
    //Перевод из дробных часов в нормальные часы минуты и секунды
    public static Calendar getTimeFromFragmentaryHours(double fragmHour){
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
        Calendar tempDate = AstroCalc.getTimeFromFragmentaryHours(fragmHour);
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

    //Дробные градусы из градусов минут и секунд
    public static double convertAngleToFragmentaryDegrees(double deg, double min, double sec){
        min = min + sec/60.0;
        deg = deg + min;
        return deg;
    }

    //вычисление постоянной B дял вычисления звездного времени
    public static double calcB(Calendar date){
        date.set(Calendar.MONTH,0);
        date.set(Calendar.DAY_OF_MONTH,1);
        //date.add(Calendar.DATE,-1);
        int year = date.get(Calendar.YEAR);
        double JD = getJD(date)-1;
        double S = JD - 2415020.0;
        double T = S/36525.0;
        double R = 6.6460656 + 2400.051262*T+0.00002581*T*T;
        double U = R - 24.0*(year - 1900);
        double B = 24 - U;
        return B;
    }

    //Перевод из Гринвичского среднего времени (GMT) в гринвичское звездное время (GST)
   public static double convertTimeGMTToGST(Calendar GMTdate){
       double A = 0.0657098;
       double C = 1.002738;
       //double D = 0.997270;
       double B = calcB(GMTdate);

       double T = (double) GMTdate.get(Calendar.DAY_OF_YEAR);
       double T0 = T*A - B;
       double fragmHourGMT = getFragmentaryHourOfDay(GMTdate);
       double fragmHourGST = fragmHourGMT*C + T0;
       if (fragmHourGST > 24) fragmHourGST -= 24;
       if (fragmHourGST < 0) fragmHourGST += 24;
       return fragmHourGST;
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
        double MD = var3 + var2 + 306001*(month+1)/1000 + day;
        MD = getJD(date) - 2400000;

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
        double T0 = (MD - 51544.5) / 36525;
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
        SunAzimuth = Az;
    }
    //Вычисление Солнца 2 вариант
    public static void getSunPos2(double lat, double lng, Calendar date){
        // 0. Некоторые табличные и постоянные
        //double Eg = 278.833540; //Эклиптическая долгота в эпоху 1980.0 в градусах
        double Eg = 289.97232199731; //Эклиптическая долгота в эпоху 2017 в градусах
        double omegag = 282.596403; //Эклиптическая долгота в перигее
        double e = 0.016718; //эксцентриситет орбиты
        double r0 = 1.495985e8; //большая полуось в км
        double fi0 = 0.533128; // угловой диаметр при r = r0 в градусах
        double Pi = 3.1415927; //число Пи

        // 1-2. Опреедлим число суток от начала данного года
        date = convertToUT(date,2);
        int D = (int) getFragmentaryOfDayByTime(date);

        // 3. Вычислим N
        double N = 360.0*D/365.2422;
        if (N > 360) N-=360;
        if (N < 360) N+=360;

        // 4.
        double M = N + Eg - omegag;
        if (M < 0) M += 360;

        // 5.
        double Ec = 360 * e * Math.sin(M) / Pi;

        // 6. Эклиптическа долгота Солнца
        double lambda = N + Ec + Eg;
        if (Ec > 360) Ec = Ec - 360;

        // 7. Прямое восхождение alfa и склонение delta
        double beta = 0;
        double alfa = Math.atan((Math.sin(lambda)*Math.cos(e) - Math.tan(beta)*Math.sin(e)) / Math.cos(lambda));
        double delta = Math.asin(Math.sin(beta)*Math.cos(e) + Math.cos(beta)*Math.sin(e)*Math.sin(lambda));

        // 8. Азимут и высота в точке наблюдения
        //Местное звездное время
        /*
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
        */
        double LST = 0; //местное звездное время
        double H = LST - alfa; //Часовой уголж
        double sin_a = Math.sin(delta)*Math.sin(lat) + Math.cos(delta)*Math.cos(lat)*Math.cos(H);
        double a = Math.asin(sin_a); //Высота
        double cos_A = (Math.sin(delta) - Math.sin(lat)*Math.sin(a)) / (Math.cos(lat)*Math.cos(a));
        double A = Math.acos(cos_A); //Азимут

        SunAzimuth = A;
        SunAltitude = a;

    }

}
