package org.who.mcheck.integration;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.delivery.schedule.util.FakeSchedule;
import org.motechproject.delivery.schedule.util.ScheduleVisualization;
import org.motechproject.delivery.schedule.util.ScheduleWithCapture;
import org.motechproject.delivery.schedule.util.SetDateAction;
import org.motechproject.model.Time;
import org.motechproject.scheduletracking.api.domain.json.ScheduleRecord;
import org.motechproject.scheduletracking.api.repository.AllSchedules;
import org.motechproject.scheduletracking.api.repository.TrackedSchedulesJsonReaderImpl;
import org.motechproject.scheduletracking.api.service.ScheduleTrackingService;
import org.motechproject.testing.utils.BaseUnitTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.util.Date;
import java.util.List;

import static org.motechproject.scheduletracking.api.domain.WindowName.*;
import static org.motechproject.util.DateUtil.newDate;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/applicationContext-mcheck-web.xml")
public class MCheckSchedulesIntegrationTest extends BaseUnitTest {

    private static final int JANUARY = 1;
    @Autowired
    private ScheduleTrackingService trackingService;
    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    private ScheduleWithCapture schedule;
    private ScheduleVisualization visualization;
    @Autowired
    private AllSchedules allSchedules;

    @Value("#{schedule_tracking['schedule.definitions.directory']}")
    private String schedulesDirectory;

    @Test
    public void shouldProvideAlertsForPNCAtTheRightTimes() throws Exception {
        schedule.enrollFor("Post Delivery Danger Signs", newDate(2012, JANUARY, 1), new Time(14, 0));

        schedule.assertNoAlerts("Day 1", earliest);
        schedule.assertAlertsStartWith("Day 1", due, date(1, JANUARY));
        schedule.assertNoAlerts("Day 1", late);
        schedule.assertNoAlerts("Day 1", max);

        schedule.assertNoAlerts("Day 2", earliest);
        schedule.assertAlertsStartWith("Day 2", due, date(2, JANUARY));
        schedule.assertNoAlerts("Day 2", late);
        schedule.assertNoAlerts("Day 2", max);

        schedule.assertNoAlerts("Day 3", earliest);
        schedule.assertAlertsStartWith("Day 3", due, date(3, JANUARY));
        schedule.assertNoAlerts("Day 3", late);
        schedule.assertNoAlerts("Day 3", max);

        schedule.assertNoAlerts("Day 4", earliest);
        schedule.assertAlertsStartWith("Day 4", due, date(4, JANUARY));
        schedule.assertNoAlerts("Day 4", late);
        schedule.assertNoAlerts("Day 4", max);

        schedule.assertNoAlerts("Day 5", earliest);
        schedule.assertAlertsStartWith("Day 5", due, date(5, JANUARY));
        schedule.assertNoAlerts("Day 5", late);
        schedule.assertNoAlerts("Day 5", max);

        schedule.assertNoAlerts("Day 6", earliest);
        schedule.assertAlertsStartWith("Day 6", due, date(6, JANUARY));
        schedule.assertNoAlerts("Day 6", late);
        schedule.assertNoAlerts("Day 6", max);

        schedule.assertNoAlerts("Day 7", earliest);
        schedule.assertAlertsStartWith("Day 7", due, date(7, JANUARY));
        schedule.assertNoAlerts("Day 7", late);
        schedule.assertNoAlerts("Day 7", max);

        visualization.outputTo("post-pregnancy-danger-signs.html", 1);
    }

    @Before
    public void setUp() throws Exception {
        FakeSchedule fakeSchedule = new FakeSchedule(trackingService, schedulerFactoryBean, new SetDateAction() {
            @Override
            public void setTheDateTo(LocalDate date) {
                mockCurrentDate(date);
            }
        });

        String outputDir = null;
        if (new File("mcheck-web").exists()) {
            outputDir = "mcheck-web/doc/schedules/";
        } else if (new File("doc").exists()) {
            outputDir = "doc/schedules/";
        }
        visualization = new ScheduleVisualization(fakeSchedule, outputDir);

        schedule = new ScheduleWithCapture(fakeSchedule, visualization);

        createAllSchedules();
    }

    public void createAllSchedules() {
        allSchedules.removeAll();
        List<ScheduleRecord> scheduleRecords = new TrackedSchedulesJsonReaderImpl().getAllSchedules(schedulesDirectory);
        for (ScheduleRecord scheduleRecord : scheduleRecords) {
            allSchedules.add(scheduleRecord);
        }
    }

    @BeforeClass
    public static void turnOffSpringLogging() {
        Logger logger = Logger.getLogger("org.springframework");
        logger.setLevel(Level.FATAL);
    }

    private Date date(int day, int month) {
        return dateWithYear(day, month, 2012);
    }

    private Date dateWithYear(int day, int month, int year) {
        return new DateTime(year, month, day, 14, 0).toDate();
    }
}
