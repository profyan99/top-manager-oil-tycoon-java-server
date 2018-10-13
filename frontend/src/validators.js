import Validator from 'validator'
import isEmpty from 'lodash/isEmpty'

export function isSignUpValid(model) {
  let errors = {
    isValid: true,
    text: ''
  };

  if (!Validator.isEmail(model.email)) {
    errors.isValid = false;
    errors.text = 'Неверный формат электронной почты';
  } else if (isEmpty(model.pass) ||
    isEmpty(model.confirmPass) ||
    isEmpty(model.name) ||
    isEmpty(model.surname) ||
    isEmpty(model.nickname)) {
    errors.isValid = false;
    errors.text = 'Не все поля заполнены';
  } else if (!isEmpty(model.pass) && !isEmpty(model.confirmPass) &&
    model.pass !== model.confirmPass) {
    errors.isValid = false;
    errors.text = 'Пароли не совпадают';
  } else if (model.pass.length < 6 ||
    model.name.length < 6 ||
    model.surname.length < 6 ||
    model.nickname.length < 6) {
    errors.isValid = false;
    errors.text = 'Данные должны быть не короче 6 символов';
  }
  return errors;
}

export function isSignInValid(model) {
  let errors = {
    isValid: true,
    text: ''
  };

  if (isEmpty(model.pass) ||
    isEmpty(model.login)) {
    errors.isValid = false;
    errors.text = 'Не все поля заполнены';
  } else if (model.pass.length < 6 ||
    model.login.length < 6) {
    errors.isValid = false;
    errors.text = 'Пароль и логин должны быть не короче 6 символов';
  }
  return errors;
}
