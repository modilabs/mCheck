package org.who.mcheck.core.repository.it;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.who.mcheck.core.domain.CallStatus;
import org.who.mcheck.core.domain.CallStatusToken;
import org.who.mcheck.core.repository.AllCallStatusTokens;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:test-applicationContext-mcheck.xml")
public class AllCallStatusTokensTest {

    @Autowired
    private AllCallStatusTokens allCallStatusTokens;

    @Test
    public void shouldFindCallStatusByPhoneNumber() throws Exception {
        CallStatusToken expectedTokenForPhone1 = new CallStatusToken("phone 1", CallStatus.Unsuccessful);
        CallStatusToken expectedTokenForPhone2 = new CallStatusToken("phone 2", CallStatus.Unsuccessful);
        allCallStatusTokens.add(expectedTokenForPhone1);
        allCallStatusTokens.add(expectedTokenForPhone2);

        CallStatusToken tokenForPhone1 = allCallStatusTokens.findByContactNumber("phone 1");
        assertEquals(expectedTokenForPhone1, tokenForPhone1);

        CallStatusToken tokenForPhone2 = allCallStatusTokens.findByContactNumber("phone 2");
        assertEquals(expectedTokenForPhone2, tokenForPhone2);
    }
}