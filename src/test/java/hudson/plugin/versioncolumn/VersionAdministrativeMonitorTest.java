package hudson.plugin.versioncolumn;

import hudson.model.Descriptor;
import hudson.model.Node;
import hudson.model.Slave;
import hudson.remoting.Launcher;
import hudson.slaves.JNLPLauncher;
import hudson.slaves.NodeProperty;
import hudson.slaves.RetentionStrategy;
import hudson.slaves.SlaveComputer;
import jenkins.model.Jenkins;
import org.apache.commons.lang.RandomStringUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class VersionAdministrativeMonitorTest {

    @Rule
    public JenkinsRule r = new JenkinsRule();
    private Jenkins jenkins;

    private VersionAdministrativeMonitor subject;

    @Before
    public void setUp() throws Exception {
        subject = new VersionAdministrativeMonitor();
        subject.jenkins = jenkins = r.jenkins;
    }

    @Test
    public void testActive() throws Exception {
        jenkins.addNode(new TestSlave("1.01"));
        jenkins.addNode(new TestSlave("1.02"));

        assertThat(subject.isActivated(), is(true));
    }

    @Test
    public void inActive() throws Exception {
        jenkins.addNode(new TestSlave(Launcher.VERSION));
        jenkins.addNode(new TestSlave(Launcher.VERSION));

        assertThat(subject.isActivated(), is(false));
    }

    private static final class TestSlave extends Slave {

        private final String slaveVersion;

        public TestSlave(String slaveVersion) throws Descriptor.FormException, IOException {
            super("test-slave-" + RandomStringUtils.random(10), "test-slave", "/tmp/test-slave", "2", Node.Mode.EXCLUSIVE,
                    "test-slave", new JNLPLauncher(), new RetentionStrategy.Always(), new ArrayList<NodeProperty<?>>());
            this.slaveVersion = slaveVersion;
        }

        @Override
        public SlaveComputer getComputer() {
            return new TestComputer(this, slaveVersion);
        }
    }

    private static final class TestComputer extends SlaveComputer {

        private final String slaveVersion;

        public TestComputer(Slave slave, String slaveVersion) {
            super(slave);
            this.slaveVersion = slaveVersion;
        }

        @Override
        public String getSlaveVersion() throws IOException, InterruptedException {
            return slaveVersion;
        }
    }
}
