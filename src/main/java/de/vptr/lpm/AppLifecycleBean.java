package de.vptr.lpm;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;

/**
 * Application lifecycle bean used for startup/shutdown hooks and initialization
 * tasks.
 */
@ApplicationScoped
public class AppLifecycleBean {

    private static final Logger LOG = LoggerFactory.getLogger(AppLifecycleBean.class);

    /**
     * Path to the ASCII art banner file in resources.
     * 
     * Banner generated at: https://www.asciiart.eu/text-to-ascii-art
     * Font: DOS Rebel, Horizontal Layout: Wide, Border: Block Frame,
     * V. Padding: 1, H. Padding: 3
     */
    private static final String BANNER_PATH = "banner.txt";

    void onStart(@Observes final StartupEvent ev) {
        final var banner = this.loadBanner();
        LOG.info("\n\n{}\n", banner);
    }

    void onStop(@Observes final ShutdownEvent ev) {
        LOG.info("Libre Project Manager is shutting down. Goodbye! o/");
    }

    /**
     * Loads the ASCII art banner from the resources folder.
     *
     * @return the banner content, or an empty string if loading fails
     */
    private String loadBanner() {
        try (final var is = Thread.currentThread().getContextClassLoader().getResourceAsStream(BANNER_PATH)) {
            if (is == null) {
                LOG.warn("Banner file not found: {}", BANNER_PATH);
                return "";
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
        } catch (final IOException e) {
            LOG.warn("Failed to load banner: {}", e.getMessage());
            return "";
        }
    }
}
