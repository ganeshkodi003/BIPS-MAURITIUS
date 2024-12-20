package com.bornfire.configuration;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Base64;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.transaction.annotation.Transactional;
import com.bornfire.entity.BIPS_PasswordManagement_Repo;
import com.bornfire.entity.BIPS_Password_Management_Entity;
import com.bornfire.entity.IPSAuditTable;
import com.bornfire.entity.IPSAuditTableRep;
import com.bornfire.entity.LoginSecurity;
import com.bornfire.entity.UserProfile;
import com.bornfire.entity.UserProfileRep;
import com.bornfire.services.LoginSecurityServices;

@Configuration
@EnableWebSecurity

public class IPSWebSecurity extends WebSecurityConfigurerAdapter {
	@Autowired
	UserProfileRep userProfileRep;

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	IPSAuditTableRep ipsAuditTableRep;

	@Autowired
	LoginSecurityServices loginSecurityService;

	@Autowired
	SequenceGenerator sequence;

	@Autowired
	BIPS_PasswordManagement_Repo bIPS_PasswordManagement_Repo;

	private static final Logger logger = LoggerFactory.getLogger(IPSWebSecurity.class);

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.authorizeRequests()
				.antMatchers("/webjars/**", "/images/**", "/login*", "/changePasswordLogin*", "/submitfirstauth",
						"/changePasswordReq", "/changePasswordloginscrren", "/freezeColumn/**", "/document/**",
						"/forgetpassword*", "/sendingmail_forget", "/sendingmail_otp", "/validtingsms_otp",
						"/sendingsms_otp", "/changePasswordotp", "/ldapList/**", "/ldapListGrp/**",
						"/getstaticmerchantupiqrcode/**", "/getstaticqrcodeMerchantMaucas/**")
				.permitAll().anyRequest().authenticated().and().formLogin().loginPage("/login").permitAll()
				.failureHandler(ipsAuthFailHandle()).successHandler(ipsAuthSuccessHandle()).usernameParameter("userid")
				.and().logout().permitAll().and().logout().logoutSuccessHandler(ipsLogoutSuccessHandler()).permitAll()
				.and().sessionManagement().maximumSessions(1).maxSessionsPreventsLogin(false);

		http.csrf().disable();
	}

	@Bean
	@Override
	public UserDetailsService userDetailsService() {
		return new UserDetailsService() {
			@Override
			public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
				Optional<UserProfile> up = userProfileRep.findByusername(username);
				if (up.isPresent()) {
					return up.get();
				} else {
					Optional<BIPS_Password_Management_Entity> up1 = bIPS_PasswordManagement_Repo.findById(username);
					if (up1.isPresent()) {
						return (UserDetails) up1.get();
					} else {
						return new UserProfile();
					}
				}
			}
		};
	}

	@Bean
	public AuthenticationFailureHandler ipsAuthFailHandle() {
		return new AuthenticationFailureHandler() {
			@Override
			public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
					AuthenticationException exception) throws IOException, ServletException {
				response.setStatus(HttpStatus.UNAUTHORIZED.value());
				response.sendRedirect("login?error=" + exception.getMessage());
			}
		};
	}

	@Bean
	public AuthenticationSuccessHandler ipsAuthSuccessHandle() {
		return new AuthenticationSuccessHandler() {
			@Override
			public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				String auditID = sequence.generateRequestUUId();
				//System.out.println("user name----" + authentication.getName());
				Optional<UserProfile> up = userProfileRep.findById(authentication.getName());
				if (up.isPresent()) {
					UserProfile user = up.get();
					user.setNo_of_attmp(0);
					user.setUser_locked_flg("N");
					userProfileRep.save(user);
					request.getSession().setAttribute("USERID", user.getUserid());
					request.getSession().setAttribute("USERIDbank", user.getUserid());
					request.getSession().setAttribute("USERNAME", user.getUsername());
					request.getSession().setAttribute("ROLEID", user.getRole_id());
					request.getSession().setAttribute("DOMAINID", user.getDomain_id());
					request.getSession().setAttribute("PERMISSIONS", user.getPermissions());
					request.getSession().setAttribute("WORKCLASS", user.getWork_class());
					request.getSession().setAttribute("BUSER", user.getUserlog_flg());

					if (user.getPhoto() != null) {
						request.getSession().setAttribute("IMAGEUSER",
								Base64.getEncoder().encodeToString(user.getPhoto()));
					} else {
						request.getSession().setAttribute("IMAGEUSER", null);
					}
					IPSAuditTable audit = new IPSAuditTable();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(user.getUserid());
					audit.setFunc_code("LOGIN");
					audit.setRemarks("Login Successfully");
					audit.setAudit_table("BIPS_USER_PROFILE");
					audit.setAudit_screen("LOGIN");
					audit.setEvent_id(user.getUserid());
					audit.setEvent_name(user.getUsername());
					audit.setModi_details("-");
					audit.setAudit_ref_no(auditID.toString());
					ipsAuditTableRep.save(audit);
					response.sendRedirect("IPSDashboard");
				} else {
					Optional<BIPS_Password_Management_Entity> up1 = bIPS_PasswordManagement_Repo
							.findById(authentication.getName());
					if (up1.isPresent()) {
						BIPS_Password_Management_Entity user1 = up1.get();
						request.getSession().setAttribute("USERID", user1.getMerchant_rep_id());
						request.getSession().setAttribute("USERID1", user1.getMerchant_rep_id());
						request.getSession().setAttribute("MER_USER_ID", user1.getMerchant_user_id());
						request.getSession().setAttribute("MER_USER_NAME", user1.getMerchant_name());
						request.getSession().setAttribute("MERUNIT", user1.getUnit_id());
						request.getSession().setAttribute("ROLEID", "MER");
						request.getSession().setAttribute("USERNAME", user1.getMer_representative_name());
						request.getSession().setAttribute("UNITID", user1.getUnit_id());
						request.getSession().setAttribute("UNITNAME", user1.getUnit_name());

						request.getSession().setAttribute("acces", user1.getPwlog_flg());
						user1.setLogin_status("Y");
						// IPSAuditTable audit = new IPSAuditTable();
						// audit.setAudit_date(new Date());
						// audit.setEntry_time(new Date());
						// audit.setEntry_user(user1.getMerchant_rep_id());
						// audit.setFunc_code("LOGIN");
						// audit.setRemarks("Login Successfully");
						// audit.setAudit_table("BIPS_PASSWORD_MANAGEMENT");
						// audit.setAudit_screen("LOGIN");
						// audit.setEvent_id(user1.getMerchant_rep_id());
						// audit.setEvent_name(user1.getMer_representative_name());
						// audit.setModi_details("-");
						// audit.setAudit_ref_no(auditID.toString());
						// ipsAuditTableRep.save(audit);
						bIPS_PasswordManagement_Repo.save(user1);
						response.sendRedirect("IPSDashboard");
					}
				}
			}
		};
	}

	@Bean
	public LogoutSuccessHandler ipsLogoutSuccessHandler() {
		return new LogoutSuccessHandler() {
			@Override
			public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
					Authentication authentication) throws IOException, ServletException {
				HttpSession session = request.getSession(false);
				if (session != null) {
					session.invalidate();
				}
				Optional<UserProfile> up = userProfileRep.findById(authentication.getName());
				if (up.isPresent()) {
					UserProfile user = up.get();
					IPSAuditTable audit = new IPSAuditTable();
					String Number1 = sequence.generateRequestUUId();
					audit.setAudit_date(new Date());
					audit.setEntry_time(new Date());
					audit.setEntry_user(user.getUserid());
					audit.setFunc_code("LOGOUT");
					audit.setRemarks("Logout Successfully");
					audit.setAudit_table("BIPS_USER_PROFILE");
					audit.setAudit_screen("LOGOUT");
					audit.setEvent_id(user.getUserid());
					audit.setEvent_name(user.getUsername());
					audit.setModi_details("-");
					audit.setAudit_ref_no(Number1.toString());
					ipsAuditTableRep.save(audit);
					response.sendRedirect("login?logout");
				} else {
					Optional<BIPS_Password_Management_Entity> up1 = bIPS_PasswordManagement_Repo
							.findById(authentication.getName());
					BIPS_Password_Management_Entity user = up1.get();
					user.setLogin_status("N");
					bIPS_PasswordManagement_Repo.save(user);
					//System.out.println(user.getLogin_status());
					// IPSAuditTable audit = new IPSAuditTable();
					// String Number1 = sequence.generateRequestUUId();
					// audit.setAudit_date(new Date());
					// audit.setEntry_time(new Date());
					// audit.setEntry_user(user.getMerchant_rep_id());
					// audit.setFunc_code("LOGOUT");
					// audit.setRemarks("Logout Successfully");
					// audit.setAudit_table("BIPS_PASSWORD_MANAGEMENT");
					// audit.setAudit_screen("LOGOUT");
					// audit.setEvent_id(user.getMerchant_rep_id());
					// audit.setEvent_name(user.getMer_representative_name());
					// audit.setModi_details("-");
					// audit.setAudit_ref_no(Number1.toString());
					// ipsAuditTableRep.save(audit);
					response.sendRedirect("login?logout");
				}

			}
		};
	}

	private Authentication authenticateUserProfile(UserProfile userProfile, String password)
			throws NoSuchAlgorithmException, InvalidKeySpecException {
		if (!userProfile.isAccountNonExpired()) {
			throw new AccountExpiredException("Account Expired");
		} else if (!userProfile.isCredentialsNonExpired()) {
			throw new CredentialsExpiredException("Credentials Expired");
		} else if (!userProfile.isAccountNonLocked()) {
			throw new LockedException("Account Locked");
		} else if (!userProfile.isEnabled()) {
			throw new DisabledException("Account Disabled");
		} else if (!userProfile.isLoginAllowed()) {
			throw new LockedException("Login Not Allowed");
		}

		else if (!userProfile.ispasswordnotexpiry()) {
			throw new LockedException("Password Expired");
		}

		else {
			if (!PasswordEncryption.validatePassword(password, userProfile.getPassword())) {
				handleInvalidPassword(userProfile);
			}
			return new UsernamePasswordAuthenticationToken(userProfile.getUserid(), password, Collections.emptyList());
		}
	}

	private Authentication authenticateBipsUser(BIPS_Password_Management_Entity bipsUser, String password) {
		if (!bipsUser.isAccountNonExpired1()) {
			throw new AccountExpiredException("Account Expired");
		} else if (!bipsUser.isCredentialsNonExpired1()) {
			throw new CredentialsExpiredException("Credentials Expired");
		} else if (!bipsUser.ispasswordnotexpiry()) {
			throw new LockedException("Password Expired");
		} else if (!bipsUser.isAccountNonLocked()) {
			throw new LockedException("Account Locked");
		} else if (!bipsUser.isUserStatus()) {
			throw new LockedException("Already in Use");
		}

		/*
		 * else if (!bipsUser.isEnabled1()) { throw new
		 * DisabledException("Account Disabled"); }
		 */ else {
			try {
				if (!PasswordEncryption.validatePassword(password, bipsUser.getPassword())) {
					handleInvalidPasswords(bipsUser);
				}
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				// Handle the exception appropriately
				e.printStackTrace();
				throw new AuthenticationServiceException("Authentication error", e);
			}
			return new UsernamePasswordAuthenticationToken(bipsUser.getMerchant_rep_id(), password,
					Collections.emptyList());
		}
	}

	private void handleInvalidPassword(UserProfile userProfile) {
		// Handle the invalid password scenario for UserProfile
		logger.info("Passing Userid :" + userProfile.getUserid());
		LoginSecurity no_attempts = loginSecurityService.getLoginSec();
		String attempts = no_attempts.getNof_atp().toString();
		Session hs = sessionFactory.getCurrentSession();
		Transaction tr = hs.getTransaction();
		hs.createQuery(
				"update UserProfile a set a.no_of_attmp=nvl(a.no_of_attmp,0)+1, a.user_locked_flg=decode(nvl(a.no_of_attmp,0)+1,?2,'Y','N'), a.login_status=decode(nvl(a.no_of_attmp,0)+1,'3','Inactive','Active') where userid=?1")
				.setParameter(1, userProfile.getUserid()).setParameter(2, attempts).executeUpdate();
		tr.commit();
		hs.close();
		throw new BadCredentialsException("Authentication failed");
	}

	private void handleInvalidPasswords(BIPS_Password_Management_Entity BIPS_Password_Management_Entity) {
		// Handle the invalid password scenario for UserProfile
		logger.info("Passing Userid :" + BIPS_Password_Management_Entity.getMerchant_rep_id());
		LoginSecurity no_attempts = loginSecurityService.getLoginSec();
		String attempts = no_attempts.getNof_atp().toString();
		Session hs = sessionFactory.getCurrentSession();
		Transaction tr = hs.getTransaction();
		hs.createQuery("update BIPS_Password_Management_Entity a set " + "a.no_of_attmp = nvl(a.no_of_attmp, 0) + 1, "
				+ "a.user_locked_flg = decode(nvl(a.no_of_attmp, 0) + 1, :attempts, 'Y', 'N'), "
				+ "a.login_status = decode(nvl(a.no_of_attmp, 0) + 1, 3, 'Inactive', 'Active') "
				+ "where merchant_rep_id = :merchantRepId")
				.setParameter("merchantRepId", BIPS_Password_Management_Entity.getMerchant_rep_id())
				.setParameter("attempts", attempts).executeUpdate();

		tr.commit();
		hs.close();
		throw new BadCredentialsException("Authentication failed");
	}

	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider ap = new DaoAuthenticationProvider() {
			@Override
			@Transactional
			public Authentication authenticate(Authentication authentication) throws AuthenticationException {
				String userid = authentication.getName();
				String password = authentication.getCredentials().toString();

				// First, try to authenticate against the first table
				Optional<UserProfile> userProfileOptional = null;
				try {
					userProfileOptional = userProfileRep.findById(userid);
				} catch (Exception e) {
					throw new LockedException("Server Problem.Please Contact Administrator");
				}

				if (userProfileOptional.isPresent()) {
					UserProfile userProfile = userProfileOptional.get();
					// Your existing authentication logic for the first table (UserProfile)
					try {
						return authenticateUserProfile(userProfile, password);
					} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					Optional<BIPS_Password_Management_Entity> bipsUserOptional = null;
					try {
						bipsUserOptional = bIPS_PasswordManagement_Repo.findById(userid);
					} catch (Exception e) {
						throw new LockedException("Server Problem.Please Contact Administrator");
					}

					if (bipsUserOptional.isPresent()) {
						BIPS_Password_Management_Entity bipsUser = bipsUserOptional.get();
						// Your authentication logic for the second table
						// (BIPS_Password_Management_Entity)
						return authenticateBipsUser(bipsUser, password);
					} else {
						throw new UsernameNotFoundException("User not found");
					}
				}
				return authentication;
			}
		};

		ap.setHideUserNotFoundExceptions(false);
		ap.setUserDetailsService(userDetailsService());
		return ap;
	}
}
