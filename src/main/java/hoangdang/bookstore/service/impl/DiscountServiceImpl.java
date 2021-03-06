package hoangdang.bookstore.service.impl;

import java.sql.Timestamp;
import java.util.List;

import hoangdang.bookstore.service.DiscountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import hoangdang.bookstore.dao.DiscountDao;
import hoangdang.bookstore.dao.UserDao;
import hoangdang.bookstore.entity.Discount;
import hoangdang.bookstore.entity.User;
import hoangdang.bookstore.model.DiscountModel;

@Service
public class DiscountServiceImpl implements DiscountService {
	@Autowired
	DiscountDao discountDao;

	@Autowired
	UserDao userDao;

	// Class cung cap service gui mail
	@Autowired
	MailerServiceImpl mailerService;

	@Override
	public DiscountModel createDiscount(DiscountModel discountModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);

		Discount discount = new Discount();

		discount.setName(discountModel.getName());
		discount.setCode(discountModel.getCode());
		discount.setPrice(discountModel.getPrice());
		discount.setApplyday(discountModel.getApplyDay());
		discount.setExpiration(discountModel.getExpiration());
		discount.setQuality(discountModel.getQuality());
		discount.setMoneylimit(discountModel.getMoneyLimit());

		discount.setPersoncreate(temp.getId());
		discount.setCreateday(timestamp.toString());
		discountDao.save(discount);
		return discountModel;
	}

	@Override
	public List<Discount> findAll() {
		return discountDao.getListDiscount();
	}

	@Override
	public DiscountModel getOneDiscountById(Integer id) {
		Discount discount = discountDao.findById(id).get();
		DiscountModel discountModel = new DiscountModel();
		discountModel.setName(discount.getName());
		discountModel.setPrice(discount.getPrice());
		discountModel.setCode(discount.getCode());
		discountModel.setApplyDay(discount.getApplyday());
		discountModel.setExpiration(discount.getExpiration());
		discountModel.setMoneyLimit(discount.getMoneylimit());
		discountModel.setQuality(discount.getQuality());
		return discountModel;
	}

	@Override
	public void delete(Integer id) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();
		User temp = userDao.findUserByEmail(username);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());

		Discount discount = discountDao.findById(id).get();
		discount.setPersondelete(temp.getId());
		discount.setDeleteday(timestamp.toString());
		discountDao.save(discount);
	}

	@Override
	public DiscountModel updateDiscount(DiscountModel discountModel) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String username = ((UserDetails) principal).getUsername();

		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		User temp = userDao.findUserByEmail(username);

		Discount discount = discountDao.findById(discountModel.getId()).get();
		discount.setName(discountModel.getName());
		discount.setCode(discountModel.getCode());
		discount.setPrice(discountModel.getPrice());
		discount.setApplyday(discountModel.getApplyDay());
		discount.setExpiration(discountModel.getExpiration());
		discount.setQuality(discountModel.getQuality());
		discount.setMoneylimit(discountModel.getMoneyLimit());

		discount.setUpdateday(timestamp.toString());
		discount.setPersonupdate(temp.getId());
		discountDao.save(discount);
		return discountModel;
	}

	@Override
	public Discount getDiscountByCode(String code) {
		return discountDao.getDiscountByCode(code);
	}

	@Override
	public void updateQuality(Discount discount) {
		if (discount != null) {
			discount.setQuality(discount.getQuality() - 1);
			discountDao.save(discount);
		}
	}

	@Override
	public List<Discount> getListDiscountAvailable() {
		return discountDao.getListDiscountAvailable();
	}

	@Override
	public User sendCodeDiscount(Integer discountId, User user) {
		Discount discount = discountDao.findById(discountId).get();
		
		String[] applyDay = discount.getApplyday().split("-");
		String resultApplyDay = applyDay[2] + "/" + applyDay[1] + "/" + applyDay[0];
		
		String[] expiration = discount.getExpiration().split("-");
		String resultExpiration = expiration[2] + "/" + expiration[1] + "/" + expiration[0];
		
		mailerService.queue(user.getEmail(), "FAHASA. Th??ng Tin Khuy???n M??i!", 
				"Xin ch??o b???n " + user.getFullname() +",<br>"
				+ "Fahasa xin th??ng b??o ?????n b???n ch????ng tr??nh. " + discount.getName() + " khi b???n nh???p m?? <b>" + discount.getCode() + "</b>." + "<br>"
				+ "Th???i gian ??p d???ng t??? ng??y " + resultApplyDay +" ?????n ng??y " + resultExpiration + "<br>"
				+ "S??? ti???n gi???m " + discount.getPrice() + "??<br>"
				+ "S??? ti???n ??p d???ng tr??n " + discount.getMoneylimit() + "??<br>"
				+ "<br><br>"
				+ "Xin ch??n th??nh c???m ??n ???? s??? d???ng d???ch v???,<br>"
				+ "FASAHA SHOP");
		return user;
	}

}
