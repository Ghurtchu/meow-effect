package experiments

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

    trait ContributorFetchingService[A](tokenProvider: TokenProvider[A]) {
      def getContributors(url: String): List[String]
    }

    class ContributorFetchingServiceLive(tokenProvider: TokenProvider[GithubToken]) extends ContributorFetchingService[GithubToken](tokenProvider) {
      override def getContributors(url: String): List[String] = {
        val githubToken = tokenProvider.provide
        // use githubToken

        "contrib.1" :: "contrib.2" :: "contrib.3" :: Nil
      }
    }
  }

}
