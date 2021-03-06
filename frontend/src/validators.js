import Validator from 'validator'
import isEmpty from 'lodash/isEmpty'

export function isSignUpValid(model) {
  let errors = {
    isValid: true,
    text: ''
  };

  if (isEmpty(model.pass) ||
    isEmpty(model.confirmPass) ||
    isEmpty(model.name) ||
    isEmpty(model.surname) ||
    isEmpty(model.nickname)) {
    errors.isValid = false;
    errors.text = 'Не все поля заполнены';
  } else if (model.pass.length < 6 ||
    model.name.length < 3 ||
    model.surname.length < 3 ||
    model.nickname.length < 6) {
    errors.isValid = false;
    errors.text = 'Данные должны быть не короче 6 символов. <br> А имя и фамилия не короче 3.';
  } else {
    errors = isPasswordValid(model);
    if (errors.isValid) {
      errors = isEmailValid(model.email);
    }
  }
  return errors;
}

export function isEmailValid(email) {
  let errors = {
    isValid: true,
    text: ''
  };

  if (!Validator.isEmail(email)) {
    errors.isValid = false;
    errors.text = 'Неверный формат электронной почты';
  }
  return errors;
}

export function isPasswordValid(model) {
  let errors = {
    isValid: true,
    text: ''
  };

  if (!isEmpty(model.pass) && !isEmpty(model.confirmPass) &&
    model.pass !== model.confirmPass) {
    errors.isValid = false;
    errors.text = 'Пароли не совпадают';
  }
  return errors;
}

export function isSignInValid(model) {
  let errors = {
    isValid: true,
    text: ''
  };

  if (isEmpty(model.password) ||
    isEmpty(model.username)) {
    errors.isValid = false;
    errors.text = 'Не все поля заполнены';
  } else if (model.password.length < 6 ||
    model.username.length < 6) {
    errors.isValid = false;
    errors.text = 'Пароль и логин должны быть не короче 6 символов';
  }
  return errors;
}

export function isEditProfileValid(model) {
  let errors = {
    isValid: true,
    text: ''
  };

  if (isEmpty(model.oldPass) && !isEmpty(model.newPass) ||
    !isEmpty(model.oldPass) && isEmpty(model.newPass)) {
    errors.isValid = false;
    errors.text = 'Если вы хотите изменить пароль, то оба поля с паролем должны быть заполнены';
  } else if (checkLength(model.name) ||
    checkLength(model.surname) ||
    checkLength(model.newPass) ||
    checkLength(model.oldPass)) {
    errors.isValid = false;
    errors.text = 'Данные должны быть не короче 6 символов';
  }
  return errors;
}

function checkLength(field) {
  return !isEmpty(field) && field.length < 6;
}
