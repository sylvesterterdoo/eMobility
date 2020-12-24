import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.Assert.*;

public class ChargingStationTest {


    @Test
    public void customerCheckStationIsOpenWhenStationIsClosed() {
        LocalDateTime time = LocalDateTime.of(2020, 12, 15, 12, 10, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());
        boolean result = getStoreChargingStation(tenant, StationType.CUSTOMER).
                isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertFalse(result);
    }

    @Test
    public void customerCheckStationIsOpenWhenStationIsOpen() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 12, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());
        boolean result = getStoreChargingStation(tenant, StationType.CUSTOMER).
                isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertTrue(result);
    }


    @org.junit.Test
    public void employeeCheckStationIsOpenWhenStationIsOpen1() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 15, 12, 10, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());
        boolean result = getStoreChargingStation(tenant, StationType.EMPLOYEE).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void employeeCheckStationIsOpenWhenStationIsClosed2() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 16, 30, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());
        boolean result = getStoreChargingStation(tenant, StationType.EMPLOYEE).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertFalse(result);
    }

    @Test
    public void employeeCheckStationIsOpenWhenStationIsOpen() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 6, 30, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());
        boolean result = getStoreChargingStation(tenant, StationType.EMPLOYEE).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void managerCheckStationIsOpenWhenStationIsOpen() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 23, 30, 20);
        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());
        boolean result = getStoreChargingStation(tenant, StationType.MANAGER).
                isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void customerCheckStationIsOpenWhenStationIsClosedOpenException() {
        LocalDateTime time = LocalDateTime.of(2020, 12, 15, 12, 10, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());

        LocalDate from = LocalDate.of(2020, 12, 15);
        LocalDate to = LocalDate.of(2020, 12, 15);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(12, 00), LocalTime.of(16, 00));

        Exception chargingStationException = new Exception(from, to, timeSpan, ExceptionType.OPEN);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.CUSTOMER);
        chargingStation.addException(chargingStationException);

        boolean result = chargingStation.isChargingStationOpenDuring(Timestamp.valueOf(time));

        Assert.assertTrue(result);
    }

    @Test
    public void customerCheckStationIsOpenWhenStationIsOpenClosedException() {

        LocalDateTime time = LocalDateTime.of(2020, 12, 23, 12, 30, 20);

        Tenant tenant = new Tenant("eMobility");
        tenant.addStore(new Store());

        LocalDate from = LocalDate.of(2020, 12, 23);
        LocalDate to = LocalDate.of(2020, 12, 25);
        TimeSpan timeSpan = new TimeSpan(LocalTime.of(9, 00), LocalTime.of(16, 00));

        Exception chargingStationException = new Exception(from, to, timeSpan, ExceptionType.CLOSE);

        ChargingStation chargingStation = getStoreChargingStation(tenant, StationType.CUSTOMER);
        chargingStation.addException(chargingStationException);

        boolean result = chargingStation.isChargingStationOpenDuring(Timestamp.valueOf(time));
        Assert.assertFalse(result);
    }



    private static ChargingStation getStoreChargingStation(Tenant tenant, StationType stationType) {
        return new ChargingStation(tenant.stores.get(0).getStoreNumber(), stationType);
    }

}