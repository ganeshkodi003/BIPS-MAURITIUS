package com.bornfire.services;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.bornfire.configuration.SequenceGenerator;
import com.bornfire.entity.BIPS_MerUserManagement_Repo;
import com.bornfire.entity.BIPS_Mer_User_Management_Entity;
import com.bornfire.entity.BankAgentTableRep;
import com.bornfire.entity.IPSUserProfileMod;
import com.bornfire.entity.MerchantMaster;
import com.bornfire.entity.MerchantMasterRep;
import com.bornfire.entity.UserProfile;
import com.bornfire.entity.UserProfileRep;

@Service
@ConfigurationProperties("output")
@Transactional
public class MandateServices {

	@Autowired
	SessionFactory sessionFactory;

	@Autowired
	BankAgentTableRep bankAgentTableRep;

	@Autowired
	SequenceGenerator sequence;

	public IPSUserProfileMod UserBlobImage(String userID) {
		Session session = sessionFactory.getCurrentSession();
		// System.out.println(userID);
		@SuppressWarnings("unchecked")
		List<IPSUserProfileMod> query = (List<IPSUserProfileMod>) session
				.createQuery("from IPSUserPreofileMod where userid=?1").setParameter(1, userID).getResultList();
		return query.get(0);

	};

	@Autowired
	UserProfileRep userProfileRep;

	public UserProfile UserBlobImages(String userID) {
		// System.out.println(userID);
		List<UserProfile> query = userProfileRep.getBlobImg(userID);
		if (query.isEmpty()) {
			return new UserProfile();
		} else {
			return query.get(0);
		}
	}

	@Autowired
	MerchantMasterRep merchantMasterRep;

	public MerchantMaster MerBlobImages(String userID) {
		// System.out.println(userID);
		List<MerchantMaster> query = merchantMasterRep.getBlobimage(userID);
		if (query.isEmpty()) {
			return new MerchantMaster();
		} else {
			return query.get(0);
		}
	}

	@Autowired
	BIPS_MerUserManagement_Repo bIPS_MerUserManagement_Repo;

	public BIPS_Mer_User_Management_Entity MerUserBlobImages(String userID) {
		// System.out.println(userID);
		List<BIPS_Mer_User_Management_Entity> query = bIPS_MerUserManagement_Repo.getBlobImage(userID);
		if (query.isEmpty()) {
			return new BIPS_Mer_User_Management_Entity();
		} else {
			return query.get(0);
		}
	}

}