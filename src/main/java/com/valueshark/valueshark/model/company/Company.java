package com.valueshark.valueshark.model.company;

import com.google.gson.Gson;
import com.valueshark.valueshark.model.PriceTarget;
import com.valueshark.valueshark.model.portfolio.PortfolioItem;

import javax.persistence.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Date;
import java.util.List;

@Entity
public class Company {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  // I wish you had used this setup instead, so the companies would be automatically updated each night in each user's portfolio!
  // My best guess is that you realized it was deleting from the user's portfolio when you would delete the companies every night.
  // To fix this, you could have updated the companies by their symbol each night, instead of completely dropping/re-creating, or you could have made it a weaker join, where a user's PortfolioItem just contained
  // the symbol of the company you wanted it to track; then, you could've pulled that company
  // by symbol when necessary.

  //this isn't necessary but leaving it in case we decide to use this for stats later
//  @OneToMany(mappedBy = "companyId")
//  public List<PortfolioItem> portfoliosThisIsIn;

  private String symbol;
  private String companyName;
  private String exchange;
  private String industry;
  @Column(columnDefinition="text")
  private String website;
  @Column(columnDefinition="text")
  private String description;
  private String CEO;
  private String sector;
  private String state;
  private String city;
  private String country;
  private double price;
  private long marketcap;
  private double week52high;
  private double week52low;
  private double week52change;
  private long sharesOutstanding;
  private double day200MovingAvg;
  private double day50MovingAvg;
  private double ttmEPS;
  private String nextEarningsDate;
  private double peRatio;
  private double beta;
  private double profitMargin;
  private long enterpriseValue;
  private double priceToBook;
  private double pegRatio;
  @Column(columnDefinition="text")
  private String logoUrl;
  private Date newsDate;
  private String newsHeadline;
  private String newsSource;
  @Column(columnDefinition="text")
  private String newsUrl;
  @Column(columnDefinition="text")
  private String newsSummary;
  private String PTupdatedDate;
  private double PTpriceTargetAverage;
  private double PTpriceTargetHigh;
  private double PTpriceTargetLow;
  private long PTnumberOfAnalysts;

  public Company() {}

  public Company(String symbol) {

    try {
      URL url = new URL("https://cloud.iexapis.com/v1/stock/" + symbol + "/advanced-stats?token=" + System.getenv("IEXCLOUD_PUSHABLETOKEN"));
      Gson gson = new Gson();
      HttpURLConnection con;
      BufferedReader in;
      try {

        // CompanyStats object used to store data from "advanced-stats' endpoint
        CompanyStats coStats;
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        // Send request to "advanced-stats endpoint and store data in coStats
        coStats = gson.fromJson(in, CompanyStats.class);
        this.marketcap = coStats.getMarketcap();
        this.week52high = coStats.getWeek52high();
        this.week52low = coStats.getWeek52low();
        this.week52change = coStats.getWeek52change();
        this.sharesOutstanding = coStats.getSharesOutstanding();
        this.day200MovingAvg = coStats.getDay200MovingAvg();
        this.day50MovingAvg = coStats.getDay50MovingAvg();
        this.ttmEPS = coStats.getTtmEPS();
        this.nextEarningsDate = coStats.getNextEarningsDate();
        this.peRatio = coStats.getPeRatio();
        this.beta = coStats.getBeta();
        this.profitMargin = coStats.getProfitMargin();
        this.enterpriseValue = (long) coStats.getEnterpriseValue();
        this.priceToBook = coStats.getPriceToBook();
        this.pegRatio = coStats.getPegRatio();
        in.close();
        con.disconnect();

        // CompanyLogo object used to store data from "logo' endpoint
        CompanyLogo coLogo;
        url = new URL("https://cloud.iexapis.com/v1/stock/" + symbol + "/logo?token=" + System.getenv("IEXCLOUD_PUSHABLETOKEN"));
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        // Send request to "logo" endpoint and store data in coStats
        coLogo = gson.fromJson(in, CompanyLogo.class);
        this.logoUrl = coLogo.getUrl();
        in.close();
        con.disconnect();

        // PriceTarget object used to store data from "price-target' endpoint
        PriceTarget priceTarget;
        url = new URL("https://cloud.iexapis.com/v1/stock/" + symbol + "/price-target?token=" + System.getenv("IEXCLOUD_PUSHABLETOKEN"));
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        // Send request to "price-target" endpoint and store data in priceTarget
        priceTarget = gson.fromJson(in, PriceTarget.class);
        this.PTupdatedDate = priceTarget.getUpdatedDate();
        this.PTpriceTargetAverage = priceTarget.getPriceTargetAverage();
        this.PTpriceTargetHigh = priceTarget.getPriceTargetHigh();
        this.PTpriceTargetLow = priceTarget.getPriceTargetLow();
        this.PTnumberOfAnalysts = priceTarget.getNumberOfAnalysts();
        in.close();
        con.disconnect();

        // CompanyNews array used to store data from "news' endpoint
        CompanyNews[] coNews;
        url = new URL("https://cloud.iexapis.com/v1/stock/" + symbol + "/news/last/1?token=" + System.getenv("IEXCLOUD_PUSHABLETOKEN"));
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        // Send request to "news" endpoint and store data in coStats
        coNews = gson.fromJson(in, CompanyNews[].class);
        if (coNews != null && coNews.length > 0) {
          this.newsDate = new Date(coNews[0].getDatetime());
          this.newsHeadline = coNews[0].getHeadline();
          this.newsSource = coNews[0].getSource();
          this.newsUrl = coNews[0].getUrl();
          this.newsSummary = coNews[0].getSummary();
        }
        in.close();
        con.disconnect();

        // CompanyPrice object used to store data from "quote' endpoint
        CompanyPrice price;
        url = new URL("https://cloud.iexapis.com/v1/stock/" + symbol + "/quote?token=" + System.getenv("IEXCLOUD_PUSHABLETOKEN"));
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        // Send request to "quote" endpoint and store data in coStats
        price = gson.fromJson(in, CompanyPrice.class);
        this.price = price.getLatestPrice();
        in.close();
        con.disconnect();

        // CompanyInfo object used to store data from "company' endpoint
        CompanyInfo coInfo;
        url = new URL("https://cloud.iexapis.com/v1/stock/" + symbol + "/company?token=" + System.getenv("IEXCLOUD_PUSHABLETOKEN"));
        con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        in = new BufferedReader(
            new InputStreamReader(con.getInputStream()));
        // Send request to "company" endpoint and store data in coStats
        coInfo = gson.fromJson(in, CompanyInfo.class);
        this.symbol = coInfo.getSymbol();
        this.companyName = coInfo.getCompanyName();
        this.exchange = coInfo.getExchange();
        this.industry = coInfo.getIndustry();
        this.website = coInfo.getWebsite();
        this.description = coInfo.getDescription();
        this.CEO = coInfo.getCEO();
        this.sector = coInfo.getSector();
        this.state = coInfo.getState();
        this.city = coInfo.getCity();
        this.country = coInfo.getCountry();
        in.close();
        con.disconnect();
      } catch (IOException e) {
        e.printStackTrace();
      }
    } catch (MalformedURLException e) {
      e.printStackTrace();
    }
  }

  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }

  public long getId() {
    return id;
  }

  public String getSymbol() {
    return symbol;
  }

  public String getCompanyName() {
    return companyName;
  }

  public String getExchange() {
    return exchange;
  }

  public String getIndustry() {
    return industry;
  }

  public String getWebsite() {
    return website;
  }

  public String getDescription() {
    return description;
  }

  public String getCEO() {
    return CEO;
  }

  public String getSector() {
    return sector;
  }

  public String getState() {
    return state;
  }

  public String getCity() {
    return city;
  }

  public String getCountry() {
    return country;
  }

  public long getMarketcap() {
    return marketcap;
  }

  public double getWeek52high() {
    return week52high;
  }

  public double getWeek52low() {
    return week52low;
  }

  public double getWeek52change() {
    return week52change;
  }

  public long getSharesOutstanding() {
    return sharesOutstanding;
  }

  public double getDay200MovingAvg() {
    return day200MovingAvg;
  }

  public double getDay50MovingAvg() {
    return day50MovingAvg;
  }

  public double getTtmEPS() {
    return ttmEPS;
  }

  public String getNextEarningsDate() {
    return nextEarningsDate;
  }

  public double getPeRatio() {
    return peRatio;
  }

  public double getBeta() {
    return beta;
  }

  public double getProfitMargin() {
    return profitMargin;
  }

  public long getEnterpriseValue() {
    return (long) enterpriseValue;
  }

  public void setEnterpriseValue(long enterpriseValue) {
    this.enterpriseValue = (long) enterpriseValue;
  }

  public double getPriceToBook() {
    return priceToBook;
  }

  public double getPegRatio() {
    return pegRatio;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public Date getNewsDate() {
    return newsDate;
  }

  public String getNewsHeadline() {
    return newsHeadline;
  }

  public String getNewsSource() {
    return newsSource;
  }

  public String getNewsUrl() {
    return newsUrl;
  }

  public String getNewsSummary() {
    return newsSummary;
  }

  public String getPTupdatedDate() {
    return PTupdatedDate;
  }

  public double getPTpriceTargetAverage() {
    return PTpriceTargetAverage;
  }

  public double getPTpriceTargetHigh() {
    return PTpriceTargetHigh;
  }

  public double getPTpriceTargetLow() {
    return PTpriceTargetLow;
  }

  public long getPTnumberOfAnalysts() {
    return PTnumberOfAnalysts;
  }

  @Override
  public String toString() {
    return "Company:" +
        "\nid=" + id +
        "\nsymbol='" + symbol +
        "\ncompanyName='" + companyName +
        "\nexchange='" + exchange +
        "\nindustry='" + industry +
        "\nwebsite='" + website +
        "\ndescription='" + description +
        "\nCEO='" + CEO +
        "\nsector='" + sector +
        "\nstate='" + state +
        "\ncity='" + city +
        "\ncountry='" + country +
        "\nprice=" + price +
        "\nmarketcap=" + marketcap +
        "\nweek52high=" + week52high +
        "\nweek52low=" + week52low +
        "\nweek52change=" + week52change +
        "\nsharesOutstanding=" + sharesOutstanding +
        "\nday200MovingAvg=" + day200MovingAvg +
        "\nday50MovingAvg=" + day50MovingAvg +
        "\nttmEPS=" + ttmEPS +
        "\nnextEarningsDate='" + nextEarningsDate +
        "\npeRatio=" + peRatio +
        "\nbeta=" + beta +
        "\nprofitMargin=" + profitMargin +
        "\nenterpriseValue=" + enterpriseValue +
        "\npriceToBook=" + priceToBook +
        "\npegRatio=" + pegRatio +
        "\nlogoUrl='" + logoUrl +
        "\nnewsDate=" + newsDate +
        "\nnewsHeadline='" + newsHeadline +
        "\nnewsSource='" + newsSource +
        "\nnewsUrl='" + newsUrl +
        "\nnewsSummary='" + newsSummary;
  }
}
