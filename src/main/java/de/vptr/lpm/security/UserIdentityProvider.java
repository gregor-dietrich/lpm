package de.vptr.lpm.security;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.vptr.lpm.entity.User;
import io.quarkus.security.AuthenticationFailedException;
import io.quarkus.security.identity.AuthenticationRequestContext;
import io.quarkus.security.identity.IdentityProvider;
import io.quarkus.security.identity.SecurityIdentity;
import io.quarkus.security.identity.request.UsernamePasswordAuthenticationRequest;
import io.quarkus.security.runtime.QuarkusPrincipal;
import io.quarkus.security.runtime.QuarkusSecurityIdentity;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

/**
 * Quarkus identity provider for username/password authentication.
 * Validates user credentials against the database and creates security
 * identities for authenticated users.
 */
@ApplicationScoped
public class UserIdentityProvider implements IdentityProvider<UsernamePasswordAuthenticationRequest> {

    private static final Logger LOG = LoggerFactory.getLogger(UserIdentityProvider.class);

    @Inject
    EntityManager entityManager;

    /**
     * Returns the type of authentication request this provider handles.
     *
     * @return the class of username/password authentication requests
     */
    @Override
    public Class<UsernamePasswordAuthenticationRequest> getRequestType() {
        return UsernamePasswordAuthenticationRequest.class;
    }

    @Override
    public Uni<SecurityIdentity> authenticate(final UsernamePasswordAuthenticationRequest request,
            final AuthenticationRequestContext context) {

        final String username = request.getUsername();
        final String password = new String(request.getPassword().getPassword());

        return Uni.createFrom().item(() -> this.authenticateUser(username, password));
    }

    @Transactional
    SecurityIdentity authenticateUser(final String username, final String password) {
        final User user = User.find("username = ?1", username).firstResult();

        if (user == null || !PasswordUtil.verifyPassword(password, user.passwordHash)) {
            throw new AuthenticationFailedException("Invalid credentials");
        }

        LOG.debug("Authenticated user: {}", username);

        return QuarkusSecurityIdentity.builder()
                .setPrincipal(new QuarkusPrincipal(username))
                .addRoles(user.roles.stream().map(r -> r.name).collect(Collectors.toSet()))
                .build();
    }
}
