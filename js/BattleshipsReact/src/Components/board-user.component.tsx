import { Component } from "react";

import UserService from "../services/user/user.service";
import EventBus from "../common/EventBus";
import React from "react";

type Props = {};

type State = {
  content: string;
}

export default class BoardUser extends Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      content: ""
    };
  }

  componentDidMount() {
    UserService.getProfileActions().then(
      response => {
        this.setState({
          content: response.data
        });
      },
      error => {
        this.setState({
          content:
            (error.response &&
              error.response.data &&
              error.response.data.message) ||
            error.message ||
            error.toString()
        });

        if (error.response && error.response.status === 401) {
          EventBus.dispatch("logout");
        }
      }
    );
  }

  render() {
    return (
      <div className="container">
        <header className="jumbotron">
          <div>HELLO</div>
          <h3>{this.state.content}</h3>
        </header>
      </div>
    );
  }
}
