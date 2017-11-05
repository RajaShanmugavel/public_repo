package com.abc.raja.usermgmt.controller;

import java.io.EOFException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.abc.raja.usermgmt.bean.CharAmount;
import com.abc.raja.usermgmt.bean.UserBean;
import com.abc.raja.usermgmt.dao.UserString;
import com.abc.raja.usermgmt.dao.UserStringDao;

@RestController
public class UserController {

	private final Logger LOG = LoggerFactory.getLogger(UserController.class);

	@Autowired
	private UserStringDao dao;

	@Autowired
	private UserBean bean;

	@RequestMapping(value = "/adduser") // http://localhost:8080/adduser?name=raja
	public @ResponseBody String addNewUser(@RequestParam String name, HttpServletRequest request) {
		UserString user = bean.newUser();
		user.setStringState("");
		user.setUserId(name + dateAsStr());
		request.getSession().setAttribute("currentUser", user.getUserId());
		dao.save(user);
		LOG.info("user details saved.");
		return "saved :" + getIdAndHash(user);
	}

	@RequestMapping(value = "/state", method = RequestMethod.GET)
	public String getCurrentState(HttpServletRequest request) {
		String userId = getUserFromSession(request);
		String state = null;
		List<UserString> listUsers = (List<UserString>) dao.findAll();
		for (UserString u : listUsers) {
			if (u.getUserId().equals(userId)) {
				state = u.getStringState();
			}
		}
		return state;
	}

	@RequestMapping(value = "/sum", method = RequestMethod.GET)
	public Integer sumOfNumbers(HttpServletRequest request) {
		String state = getCurrentState(request);
		int sum = findSumOfNumbersInString(state);
		LOG.info("sumOfNumbers:" + sum);
		return sum;
	}

	@RequestMapping(value = "/chars", method = RequestMethod.GET)
	public String charsFromString(HttpServletRequest request) {
		String state = getCurrentState(request);
		String chars = findCharsFromString(state);
		LOG.info("charsFromString:" + chars);
		return chars;
	}

	@RequestMapping(value = "/chars", method = RequestMethod.POST)
	public String addCharsToString(@RequestBody List<CharAmount> charAmount, HttpServletRequest request) {
		String userId = getUserFromSession(request);
		String state = getCurrentState(request);
		String addedState = null;
		try {
			addedState = addToState(state, charAmount);
		} catch (NullPointerException e) {
			LOG.info("Exception", e);
			throw new NullPointerException("Json argument invalid");
		} catch (EOFException e) {
			try {
				LOG.info("Exception", e);
				throw new EOFException("Json argument invalid");
			} catch (EOFException e1) {
				LOG.info("Exception", e1);
				e1.printStackTrace();
			}
		}
		LOG.info("addedState:" + addedState);
		UserString usr = findAndSave(addedState, userId);
		LOG.info("User:" + usr.getUserId() + ", Added String:" + addedState);
		return addedState;
	}

	@RequestMapping(value = "/chars/{ch}", method = RequestMethod.GET, consumes = MediaType.ALL_VALUE)
	public String deleteCharsFromString(@PathVariable String ch, HttpServletRequest request) {
		if (isStringInvalid(ch) || ch.length() > 1 || !isStringAlphaNumeric(ch)) {
			throw new IllegalArgumentException("The delete parameter length should be 1");
		}
		String userId = getUserFromSession(request);
		String state = getCurrentState(request);
		String newState = removeChars(state, ch);
		UserString usr = findAndSave(newState, userId);
		LOG.info("User:" + usr.getUserId() + ", Deleted chars:" + ch + ", from String :" + state);
		LOG.info("New String state:" + newState);
		return newState;
	}

	@ExceptionHandler({ IllegalArgumentException.class, EOFException.class, NullPointerException.class })
	void handleBadRequests(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	private String getUserFromSession(HttpServletRequest request) {
		return request.getSession().getAttribute("currentUser").toString();
	}

	private String getIdAndHash(UserString user) {
		return "userId:" + user.getUserId() + ", hash:" + user.hashCode();
	}

	private String dateAsStr() {
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(date);
	}

	private UserString findAndSave(String newState, String userId) {
		List<UserString> listUsers = (List<UserString>) dao.findAll();
		UserString uString = new UserString();
		for (UserString usr : listUsers) {
			if (usr.getUserId().equals(userId)) {
				usr.setStringState(newState);
				usr.setUserId(userId);
				if (newState.length() > 200) {
					throw new IllegalArgumentException("The state string length should not be greater than 200");
				}
				dao.save(usr);
				uString = usr;
			}
		}
		return uString;
	}

	private String removeChars(String state, String chars) {
		if (state.lastIndexOf(chars) != -1) {
			state = new StringBuilder(state)
					.replace(state.lastIndexOf(chars), state.lastIndexOf(chars) + chars.length(), "").toString();
		}
		return state;
	}

	private String addToState(String state, List<CharAmount> charAmount)
			throws NullPointerException, EOFException, IllegalArgumentException {
		for (CharAmount ca : charAmount) {
			validateParams(ca);
			int cnt = ca.getAmount();
			LOG.info("Added the below string " + cnt + " times");
			for (int i = 0; i < cnt; i++) {
				state += ca.getCharacter();
			}
		}
		return state;
	}

	private void validateParams(CharAmount ca) {
		int amt = ca.getAmount();
		if (amt < 1 && amt > 10) {
			throw new IllegalArgumentException("Amount value should be from 1 to 9");
		}
		if (ca.getCharacter().length() > 1) {
			throw new IllegalArgumentException("Character length should be 1");
		}
	}

	private int findSumOfNumbersInString(String state) {
		int sum = 0;
		if (isStringInvalid(state) || isStringWithoutNumbers(state)) {
			return sum;
		}
		String str = state.replaceAll("[^0-9]+", " ");
		for (String s : Arrays.asList(str.trim().split(" "))) {
			sum += Integer.parseInt(s);
		}
		return sum;
	}

	private boolean isStringWithoutNumbers(String state) {
		if (state.matches("[a-zA-Z]+$")) {
			return true;
		}
		return false;
	}

	private boolean isStringInvalid(String state) {
		return StringUtils.isBlank(state);
	}

	private boolean isStringAlphaNumeric(String state) {
		return StringUtils.isAlphanumeric(state);
	}

	private String findCharsFromString(String state) {
		String onlyChars = "";
		if (isStringInvalid(state)) {
			return onlyChars;
		}
		String str = state.replaceAll("[0-9]+", " ");
		for (String s : Arrays.asList(str.trim().split(" "))) {
			onlyChars += s;
		}
		return onlyChars;
	}

	private String getRandomString() {
		String sbChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder sb = new StringBuilder();
		Random rnd = new Random();
		while (sb.length() < 18) {
			int index = (int) (rnd.nextFloat() * sbChars.length());
			sb.append(sbChars.charAt(index));
		}
		String sbStr = sb.toString();
		LOG.info("new random string generated:" + sbStr);
		return sbStr;

	}

}
