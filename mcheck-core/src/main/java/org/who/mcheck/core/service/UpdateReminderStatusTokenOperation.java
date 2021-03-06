package org.who.mcheck.core.service;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.motechproject.decisiontree.core.FlowSession;
import org.motechproject.decisiontree.core.model.INodeOperation;
import org.motechproject.decisiontree.server.domain.FlowSessionRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.who.mcheck.core.ApplicationContextUtils;
import org.who.mcheck.core.domain.ReminderStatusToken;
import org.who.mcheck.core.repository.AllReminderStatusTokens;

import java.text.MessageFormat;

import static java.text.MessageFormat.format;
import static org.apache.commons.lang.exception.ExceptionUtils.getFullStackTrace;

@Component
public class UpdateReminderStatusTokenOperation implements INodeOperation {

    private final Log log = LogFactory.getLog(UpdateReminderStatusTokenOperation.class);
    private AllReminderStatusTokens allReminderStatusTokens;

    public UpdateReminderStatusTokenOperation() {
        ApplicationContext applicationContext = ApplicationContextUtils.getApplicationContext();
        log.info("AllReminderStatusTokens is null, so autowiring it");
        allReminderStatusTokens = applicationContext.getBean(AllReminderStatusTokens.class);
    }

    @Autowired
    public UpdateReminderStatusTokenOperation(AllReminderStatusTokens allReminderStatusTokens) {
        this.allReminderStatusTokens = allReminderStatusTokens;
    }

    @Override
    public void perform(String userInput, FlowSession session) {
        try {
            ReminderStatusToken token = allReminderStatusTokens.findByContactNumber(session.getPhoneNumber());
            if (token == null) {
                log.warn(format("Could not find a call status token for the phone number: {0}. Flow session: {1}",
                        session.getPhoneNumber(), session));
                return;
            }
            log.info(
                    format("Found a call status for phone number: {0}, Updating the status to successful. Call detail: {1}",
                            session.getPhoneNumber(),
                            ToStringBuilder.reflectionToString(((FlowSessionRecord) session).getCallDetailRecord())));
            token.markCallStatusAsSuccessful();
            allReminderStatusTokens.update(token);
        } catch (Exception e) {
            log.error(MessageFormat.format("Error when executing UpdateReminderStatusTokenOperation. Message: {0}, Stack trace: {1}",
                    e.getMessage(), getFullStackTrace(e)));
        }
    }
}
