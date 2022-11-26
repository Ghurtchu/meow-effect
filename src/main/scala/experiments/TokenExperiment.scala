package experiments

import experiments.TokenExperiment.second_approach.ContributorFetchService

object TokenExperiment {

  object first_approach {
    trait TokenProvider[A] {
      def provide: A
    }

    final case class GithubToken(token: String)

    class GithubTokenProvider extends TokenProvider[GithubToken] {
      override def provide: GithubToken = {
        // do some stuff
        GithubToken("github.token")
      }
    }

    trait ContributorFetchingService {
      def getContributors(url: String): List[String]
    }

    class ContributorFetchingServiceLive(tokenProvider: TokenProvider[GithubToken]) extends ContributorFetchingService {
      override def getContributors(url: String): List[String] = {
        val githubToken = tokenProvider.provide
        // use githubToken

        "contrib.1" :: "contrib.2" :: "contrib.3" :: Nil
      }
    }
  }

  object second_approach {

    trait TokenReader[A] {
      def read: A
    }

    final case class GithubToken(value: String)

    class GithubTokenReader extends TokenReader[GithubToken] {
      override def read: GithubToken = GithubToken("github.token")
    }

    final case class Contributor(name: String)

    trait ContributorFetchService {
      def fetch[A](url: String)(implicit tokenReader: TokenReader[A]): List[String]
    }

    class Impl extends ContributorFetchService {
      override def fetch[A](url: String)(implicit tokenReader: TokenReader[A]): List[String] = {
        val token = tokenReader.read
        // use it

        "contrib.1" :: "contrib.2" :: "contrib.3" :: Nil
      }
    }

  }

}
