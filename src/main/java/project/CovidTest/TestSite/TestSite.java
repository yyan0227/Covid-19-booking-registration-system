package project.CovidTest.TestSite;

public interface TestSite {
    // to allow clients to get new instances (to avoid privacy leak)
    TestSite cloneInstance();
    String getSuburb();
    boolean provideWalkIn();
    boolean provideDriveThrough();
    boolean isClinic();
    boolean isHospital();
    boolean isGP();
}
